package mz.co.bankchallenge.banking_api.domain.transfer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mz.co.bankchallenge.banking_api.domain.transaction.BankTransaction;
import java.util.UUID;

@Entity @Table(name = "transfers") @Getter @Setter
public class Transfer {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false, unique = true)
    private BankTransaction transaction;
    @Column(name = "source_account_id", nullable = false) private UUID sourceAccountId;
    @Column(name = "destination_account_id", nullable = false) private UUID destinationAccountId;
}
