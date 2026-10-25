package mz.co.bankchallenge.banking_api.repositories;
import mz.co.bankchallenge.banking_api.domain.movement.AccountMovement;
import mz.co.bankchallenge.banking_api.enums.MovementDirection;
import mz.co.bankchallenge.banking_api.enums.TransactionType;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.UUID;

public interface AccountMovementRepository extends JpaRepository<AccountMovement, Long> {
    long countByAccountId(UUID accountId);
    @Query("""
    SELECT m FROM AccountMovement m
    JOIN m.transaction t
    WHERE m.accountId = :accountId
      AND m.createdAt >= :fromDate
      AND m.createdAt < :toDate
      AND t.type IN :types
      AND m.direction IN :directions
    """)
    Page<AccountMovement> search(
            @Param("accountId") UUID accountId,
            @Param("fromDate") OffsetDateTime fromDate,
            @Param("toDate") OffsetDateTime toDate,
            @Param("types") Collection<TransactionType> types,
            @Param("directions") Collection<MovementDirection> directions,
            Pageable pageable
    );
}
