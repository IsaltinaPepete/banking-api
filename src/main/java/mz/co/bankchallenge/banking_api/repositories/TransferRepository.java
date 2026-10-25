package mz.co.bankchallenge.banking_api.repositories;
import mz.co.bankchallenge.banking_api.domain.transfer.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
/** Repository for transfer records associated with banking transactions. */
public interface TransferRepository extends JpaRepository<Transfer, UUID> {}
