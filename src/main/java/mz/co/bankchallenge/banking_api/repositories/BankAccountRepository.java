package mz.co.bankchallenge.banking_api.repositories;

import jakarta.persistence.LockModeType;
import mz.co.bankchallenge.banking_api.domain.bankAccount.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    boolean existsByAccountNumber(String accountNumber);
    boolean existsByIdAndCustomer_Id(UUID accountId, UUID customerId);

    Page<BankAccount> findByCustomerId(UUID customerId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from BankAccount a where a.id = :id")
    Optional<BankAccount> findByIdForUpdate(@Param("id") UUID id);
}

