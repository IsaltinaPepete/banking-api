package mz.co.bankchallenge.banking_api.mappers;


import mz.co.bankchallenge.banking_api.domain.bankAccount.BankAccount;
import mz.co.bankchallenge.banking_api.domain.customer.Customer;
import mz.co.bankchallenge.banking_api.dto.response.CreateCustomerResponse;
import org.springframework.stereotype.Component;

@Component
public class CreateCustomerMapper {

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