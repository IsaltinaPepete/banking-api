package mz.co.bankchallenge.banking_api.dto.response;
import java.math.BigDecimal;
import java.util.UUID;
/** Public representation of an account balance. */
public record BalanceResponse(UUID accountId, String currency, BigDecimal balance) {}
