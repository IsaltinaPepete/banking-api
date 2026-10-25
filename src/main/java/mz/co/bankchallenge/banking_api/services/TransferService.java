package mz.co.bankchallenge.banking_api.services;
import lombok.RequiredArgsConstructor;
import mz.co.bankchallenge.banking_api.domain.bankAccount.BankAccount;
import mz.co.bankchallenge.banking_api.domain.transaction.BankTransaction;
import mz.co.bankchallenge.banking_api.domain.transfer.Transfer;
import mz.co.bankchallenge.banking_api.domain.movement.AccountMovement;
import mz.co.bankchallenge.banking_api.dto.request.CreateTransferRequest;
import mz.co.bankchallenge.banking_api.dto.response.TransferResponse;
import mz.co.bankchallenge.banking_api.enums.*;
import mz.co.bankchallenge.banking_api.repositories.AccountMovementRepository;
import mz.co.bankchallenge.banking_api.repositories.BankAccountRepository;
import mz.co.bankchallenge.banking_api.repositories.BankTransactionRepository;
import mz.co.bankchallenge.banking_api.repositories.TransferRepository;
import mz.co.bankchallenge.banking_api.repositories.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service @RequiredArgsConstructor
/** Executes atomic transfers and records the resulting movements. */
public class TransferService {
    private final BankAccountRepository accounts;
    private final BankTransactionRepository transactions;
    private final TransferRepository transfers;
    private final AccountMovementRepository movements;
    private final AppUserRepository appUserRepository;

    @Transactional
    /**
     * Transfers funds between two accounts owned by the caller or by an administrator.
     * Both account rows are locked in a deterministic order to reduce deadlocks;
     * balance changes and movement records share the surrounding transaction.
     *
     * @param req source, destination, amount and description of the transfer
     * @param authentication authenticated caller used for ownership checks
     * @return transfer reference and resulting operation details
     * @throws ResponseStatusException with {@code 400} for invalid accounts, amounts or currencies
     * @throws ResponseStatusException with {@code 403} when the caller does not own the source account
     * @throws ResponseStatusException with {@code 404} when an account is absent
     * @throws ResponseStatusException with {@code 409} when the source balance is insufficient
     */
    public TransferResponse transfer(CreateTransferRequest req, Authentication authentication) {
        UUID sourceId = req.sourceAccountId();
        UUID destId = req.destinationAccountId();
        if (sourceId.equals(destId)) throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Source and destination must differ");
        
        BigDecimal amount = req.amount();

        if (amount == null || amount.signum() <= 0 || amount.scale() > 2)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid amount");

        ensureSourceAccountOwnership(sourceId, authentication);

        // Acquire both row locks in a consistent order to reduce deadlocks.
        UUID firstId = sourceId.compareTo(destId) < 0 ? sourceId : destId;
        UUID secondId = sourceId.compareTo(destId) < 0 ? destId : sourceId;
        BankAccount first = lock(firstId);
        BankAccount second = lock(secondId);
        BankAccount source = sourceId.equals(firstId) ? first : second;
        BankAccount dest = destId.equals(firstId) ? first : second;
        if (!source.getCurrency().equals(dest.getCurrency()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Currency mismatch");
        if (source.getBalance().compareTo(amount) < 0)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient funds");

        source.setBalance(source.getBalance().subtract(amount));
        dest.setBalance(dest.getBalance().add(amount));

        BankTransaction transaction = new BankTransaction();
        transaction.setType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setAmount(amount);
        transaction.setCurrency(source.getCurrency());
        transaction.setDescription(req.description());
        // initiatedBy remains null in this demo until Keycloak identity is integrated.
        transaction.setCompletedAt(OffsetDateTime.now());
        transaction = transactions.save(transaction);

        Transfer transfer = new Transfer();
        transfer.setTransaction(transaction);
        transfer.setSourceAccountId(sourceId);
        transfer.setDestinationAccountId(destId);
        transfers.save(transfer);

        movements.save(movement(transaction, source, MovementDirection.DEBIT, amount));
        movements.save(movement(transaction, dest, MovementDirection.CREDIT, amount));
        accounts.save(source);
        accounts.save(dest);
        return new TransferResponse(transaction.getReference(), sourceId, destId,
            amount, transaction.getCurrency(), transaction.getStatus().name(), transaction.getCompletedAt());
    }

    private void ensureSourceAccountOwnership(UUID accountId, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_role_admin"));

        if (isAdmin) {
            return;
        }

        String keycloakUserId = authentication.getName();
        UUID customerId = appUserRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "User is not linked to a customer"))
                .getCustomer().getId();

        if (!accounts.existsByIdAndCustomer_Id(accountId, customerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have permission to transfer from this account");
        }
    }

    private BankAccount lock(UUID id) {
        return accounts.findByIdForUpdate(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found: " + id));
    }
    private AccountMovement movement(BankTransaction transaction, BankAccount account,
                                      MovementDirection direction, BigDecimal amount) {
        AccountMovement moviment = new AccountMovement();
        moviment.setTransaction(transaction);
        moviment.setAccountId(account.getId());
        moviment.setDirection(direction);
        moviment.setAmount(amount);
        moviment.setBalanceAfter(account.getBalance());
        return moviment;
    }
}
