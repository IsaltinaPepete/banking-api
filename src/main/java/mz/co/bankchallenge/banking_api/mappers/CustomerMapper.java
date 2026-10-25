package mz.co.bankchallenge.banking_api.mappers;


import mz.co.bankchallenge.banking_api.domain.customer.Customer;
import mz.co.bankchallenge.banking_api.dto.response.CustomerResponse;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getNuit()
        );
    }
}