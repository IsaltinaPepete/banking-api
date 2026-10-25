package mz.co.bankchallenge.banking_api.repositories;
import mz.co.bankchallenge.banking_api.domain.transaction.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
/** Repository for persisted banking transactions. */
public interface BankTransactionRepository extends JpaRepository<BankTransaction, UUID> {
    /** @return whether an idempotency key is already stored */
    boolean existsByIdempotencyKey(UUID idempotencyKey);
}
