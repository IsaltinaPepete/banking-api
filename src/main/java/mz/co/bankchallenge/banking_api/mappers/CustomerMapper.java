package mz.co.bankchallenge.banking_api.mappers;


import mz.co.bankchallenge.banking_api.domain.customer.Customer;
import mz.co.bankchallenge.banking_api.dto.response.CustomerResponse;
import org.springframework.stereotype.Component;

@Component
/** Maps customer persistence entities to API response DTOs. */
public class CustomerMapper {

    /** @param customer customer entity
     *  @return customer response DTO
     */
    public CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getNuit()
        );
    }
}
