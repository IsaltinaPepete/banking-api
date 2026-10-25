package mz.co.bankchallenge.banking_api.dto.response;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
public record TransferResponse(UUID reference, UUID sourceAccountId,
    UUID destinationAccountId, BigDecimal amount, String currency,
    String status, OffsetDateTime completedAt) {}
