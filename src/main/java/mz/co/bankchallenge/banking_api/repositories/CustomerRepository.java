package mz.co.bankchallenge.banking_api.repositories;

import mz.co.bankchallenge.banking_api.domain.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    boolean existsByNuit(String nuit);
}