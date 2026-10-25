package mz.co.bankchallenge.banking_api.services;
import lombok.RequiredArgsConstructor;
import mz.co.bankchallenge.banking_api.repositories.BankAccountRepository;
import mz.co.bankchallenge.banking_api.repositories.AppUserRepository;
import mz.co.bankchallenge.banking_api.domain.bankAccount.BankAccount;
import mz.co.bankchallenge.banking_api.dto.response.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository accounts;
    private final AppUserRepository appUserRepository;

    @Transactional(readOnly = true)
    public Page<AccountResponse> findAll(Pageable pageable) {
        return accounts.findAll(pageable).map(this::toResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<AccountResponse> findByCustomer(UUID customerId, Pageable pageable) {
        return accounts.findByCustomerId(customerId, pageable).map(this::toResponse);
    }
    @Transactional(readOnly = true)
    public AccountResponse get(UUID id, Authentication authentication) {
        BankAccount account = findAccount(id);
        ensureCanAccess(account, authentication);
        return toResponse(account);
    }
    @Transactional(readOnly = true)
    public BalanceResponse balance(UUID id, Authentication authentication) {
        BankAccount a = findAccount(id);
        ensureCanAccess(a, authentication);
        return new BalanceResponse(a.getId(), a.getCurrency(), a.getBalance());
    }

    private void ensureCanAccess(BankAccount account, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_role_admin"));

        if (isAdmin) {
            return;
        }

        String keycloakUserId = authentication.getName();
        UUID customerId = appUserRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "User is not linked to a customer"))
                .getCustomer().getId();

        if (!account.getCustomer().getId().equals(customerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have permission to access this account");
        }
    }
    private BankAccount findAccount(UUID id) {
        return accounts.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }
    private AccountResponse toResponse(BankAccount a) {
        return new AccountResponse(a.getId(), a.getCustomer().getId(),
            a.getAccountNumber(), a.getAccountType(), a.getCurrency(), a.getBalance());
    }
}
