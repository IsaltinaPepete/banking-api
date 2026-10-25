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

/** Repository for account persistence, ownership checks and row locking. */
public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    /** @return whether the generated account number is already used */
    boolean existsByAccountNumber(String accountNumber);
    /** @return whether the account belongs to the specified customer */
    boolean existsByIdAndCustomer_Id(UUID accountId, UUID customerId);

    /** @return a page of accounts owned by the specified customer */
    Page<BankAccount> findByCustomerId(UUID customerId, Pageable pageable);

    /** Loads an account with a pessimistic write lock for balance changes. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from BankAccount a where a.id = :id")
    Optional<BankAccount> findByIdForUpdate(@Param("id") UUID id);
}

