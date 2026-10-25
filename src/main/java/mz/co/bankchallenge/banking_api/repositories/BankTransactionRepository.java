package mz.co.bankchallenge.banking_api.repositories;
import mz.co.bankchallenge.banking_api.domain.transaction.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface BankTransactionRepository extends JpaRepository<BankTransaction, UUID> {
    boolean existsByIdempotencyKey(UUID idempotencyKey);
}
