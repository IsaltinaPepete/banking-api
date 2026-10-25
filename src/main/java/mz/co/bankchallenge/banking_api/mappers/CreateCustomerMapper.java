package mz.co.bankchallenge.banking_api.mappers;


import mz.co.bankchallenge.banking_api.domain.bankAccount.BankAccount;
import mz.co.bankchallenge.banking_api.domain.customer.Customer;
import mz.co.bankchallenge.banking_api.dto.response.CreateCustomerResponse;
import org.springframework.stereotype.Component;

@Component
/** Maps a newly persisted customer and account to the creation response. */
public class CreateCustomerMapper {

    /**
     * @param customer persisted customer
     * @param account persisted bank account
     * @return combined customer and account response
     */
    public CreateCustomerResponse toResponse(Customer customer, BankAccount account) {
        return new CreateCustomerResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getNuit(),
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getCurrency(),
                account.getBalance()
        );
    }
}
