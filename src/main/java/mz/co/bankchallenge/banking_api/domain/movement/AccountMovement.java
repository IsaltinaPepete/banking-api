package mz.co.bankchallenge.banking_api.domain.movement;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.co.bankchallenge.banking_api.domain.transaction.BankTransaction;
import mz.co.bankchallenge.banking_api.enums.MovementDirection;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name = "account_movements") @Getter @Setter
/** Ledger entry representing one credit or debit applied to an account. */
public class AccountMovement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false) private BankTransaction transaction;
    @Column(name = "account_id", nullable = false) private UUID accountId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 10) private MovementDirection direction;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal amount;
    @Column(name = "balance_after", nullable = false, precision = 19, scale = 2) private BigDecimal balanceAfter;
    @Column(name = "created_at", nullable = false) private OffsetDateTime createdAt;
    @PrePersist void beforeInsert() { if (createdAt == null) createdAt = OffsetDateTime.now(); }
}
