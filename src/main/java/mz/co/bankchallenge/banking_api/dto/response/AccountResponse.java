package mz.co.bankchallenge.banking_api.dto.response;
import mz.co.bankchallenge.banking_api.enums.AccountType;
import java.math.BigDecimal;
import java.util.UUID;
/** Public representation of a bank account and its current balance. */
public record AccountResponse(UUID id, UUID customerId, String accountNumber,
    AccountType accountType, String currency, BigDecimal balance) {}
