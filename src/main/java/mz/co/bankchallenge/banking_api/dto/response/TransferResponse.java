package mz.co.bankchallenge.banking_api.dto.response;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
/** Public representation of a completed transfer. */
public record TransferResponse(UUID reference, UUID sourceAccountId,
    UUID destinationAccountId, BigDecimal amount, String currency,
    String status, OffsetDateTime completedAt) {}
