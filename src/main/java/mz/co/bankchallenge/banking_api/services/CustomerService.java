package mz.co.bankchallenge.banking_api.services;

import lombok.RequiredArgsConstructor;
import mz.co.bankchallenge.banking_api.domain.bankAccount.BankAccount;
import mz.co.bankchallenge.banking_api.domain.customer.Customer;
import mz.co.bankchallenge.banking_api.dto.request.CreateCustomerRequest;
import mz.co.bankchallenge.banking_api.dto.response.CreateCustomerResponse;
import mz.co.bankchallenge.banking_api.dto.response.CustomerResponse;
import mz.co.bankchallenge.banking_api.exception.ResourceAlreadyExistsException;
import mz.co.bankchallenge.banking_api.mappers.CreateCustomerMapper;
import mz.co.bankchallenge.banking_api.mappers.CustomerMapper;
import mz.co.bankchallenge.banking_api.repositories.BankAccountRepository;
import mz.co.bankchallenge.banking_api.repositories.CustomerRepository;
import mz.co.bankchallenge.banking_api.repositories.AppUserRepository;
import mz.co.bankchallenge.banking_api.utils.AccountNumberGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
/** Coordinates customer and initial bank-account creation and customer queries. */
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AppUserRepository appUserRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final CreateCustomerMapper createCustomerMapper;
    private final CustomerMapper customerMapper;

    @Transactional
    /**
     * Creates a customer and one empty bank account in the same transaction.
     *
     * <p>The NUIT is checked before persistence and the generated account number
     * is verified against existing accounts.</p>
     *
     * @param request customer and account-type data
     * @return the persisted customer and account data
     * @throws ResourceAlreadyExistsException if the NUIT is already registered
     */
    public CreateCustomerResponse createCustomer(CreateCustomerRequest request) {

        if (customerRepository.existsByNuit(request.nuit())) {
            throw new ResourceAlreadyExistsException("Customer with this NUIT already exists");
        }

        Customer customer = new Customer();
        customer.setFullName(request.fullName().trim());
        customer.setNuit(request.nuit().trim());

        customer = customerRepository.save(customer);

        BankAccount account = new BankAccount();
        account.setCustomer(customer);
        account.setAccountType(request.accountType());
        account.setCurrency("MZN");
        account.setBalance(BigDecimal.ZERO);
        account.setAccountNumber(generateUniqueAccountNumber());

        account = bankAccountRepository.save(account);

        return createCustomerMapper.toResponse(customer, account);
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = accountNumberGenerator.generate();
        } while (bankAccountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    @Transactional(readOnly = true)
    /** @param pageable pagination and sorting instructions
     *  @return a page of customers
     */
    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(customerMapper::toResponse);
    }

    @Transactional(readOnly = true)
    /**
     * Returns a customer after checking ownership for non-administrative users.
     *
     * @param id customer identifier
     * @param authentication authenticated caller and its Keycloak identity
     * @return the requested customer
     * @throws ResponseStatusException with {@code 404} when the customer does not exist
     * @throws ResponseStatusException with {@code 403} when the caller is not the owner
     */
    public CustomerResponse getCustomerById(UUID id, Authentication authentication) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Customer not found"
                        )
                );

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_role_admin"));

        if (!isAdmin) {
            String keycloakUserId = authentication.getName();
            UUID ownerId = appUserRepository.findByKeycloakUserId(keycloakUserId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.FORBIDDEN, "User is not linked to a customer"))
                    .getCustomer().getId();

            if (!ownerId.equals(customer.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You do not have permission to access this customer");
            }
        }

        return customerMapper.toResponse(customer);
    }
}
