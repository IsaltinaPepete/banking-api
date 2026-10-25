package mz.co.bankchallenge.banking_api.services;
import lombok.RequiredArgsConstructor;
import mz.co.bankchallenge.banking_api.domain.bankAccount.BankAccount;
import mz.co.bankchallenge.banking_api.domain.transaction.BankTransaction;
import mz.co.bankchallenge.banking_api.domain.movement.AccountMovement;
import mz.co.bankchallenge.banking_api.dto.request.DepositRequest;
import mz.co.bankchallenge.banking_api.dto.response.BalanceResponse;
import mz.co.bankchallenge.banking_api.enums.*;
import mz.co.bankchallenge.banking_api.repositories.AccountMovementRepository;
import mz.co.bankchallenge.banking_api.repositories.BankAccountRepository;
import mz.co.bankchallenge.banking_api.repositories.BankTransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class DepositService {
    private final BankAccountRepository accounts;
    private final BankTransactionRepository transactions;
    private final AccountMovementRepository movements;
    @Transactional
    public BalanceResponse deposit(UUID accountId, DepositRequest req) {

        BigDecimal amount = req.amount();

        if (amount == null || amount.signum() <= 0 || amount.scale() > 2)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid amount");

        BankAccount account = accounts.findByIdForUpdate(accountId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        boolean initial = movements.countByAccountId(accountId) == 0;
        BankTransaction tx = new BankTransaction();
        tx.setType(initial ? TransactionType.INITIAL_DEPOSIT : TransactionType.DEPOSIT);
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setAmount(amount);
        tx.setCurrency(account.getCurrency());
        tx.setDescription(req.description());
        tx.setCompletedAt(OffsetDateTime.now());
        tx = transactions.save(tx);
        account.setBalance(account.getBalance().add(amount));
        AccountMovement m = new AccountMovement();
        m.setTransaction(tx);
        m.setAccountId(accountId);
        m.setDirection(MovementDirection.CREDIT);
        m.setAmount(amount);
        m.setBalanceAfter(account.getBalance());
        movements.save(m);
        accounts.save(account);
        return new BalanceResponse(accountId, account.getCurrency(), account.getBalance());
    }
}
