package mz.co.bankchallenge.banking_api.domain.transaction;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.co.bankchallenge.banking_api.enums.TransactionStatus;
import mz.co.bankchallenge.banking_api.enums.TransactionType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name = "transactions") @Getter @Setter
public class BankTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private UUID reference = UUID.randomUUID();
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private TransactionType type;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private TransactionStatus status;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal amount;
    @JdbcTypeCode(SqlTypes.CHAR) @Column(nullable = false, length = 3) private String currency = "MZN";
    @Column(length = 255) private String description;
    @Column(name = "initiated_by") private UUID initiatedBy;
    @Column(name = "idempotency_key", unique = true) private UUID idempotencyKey;
    @Column(name = "created_at", nullable = false) private OffsetDateTime createdAt;
    @Column(name = "completed_at") private OffsetDateTime completedAt;
    @PrePersist void beforeInsert() { if (createdAt == null) createdAt = OffsetDateTime.now(); }
}
