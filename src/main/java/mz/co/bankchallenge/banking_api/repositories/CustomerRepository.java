package mz.co.bankchallenge.banking_api.repositories;

import mz.co.bankchallenge.banking_api.domain.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/** Repository for customer persistence and NUIT uniqueness checks. */
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    /** @return whether the NUIT is already registered */
    boolean existsByNuit(String nuit);
}
