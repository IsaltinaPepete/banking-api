package mz.co.bankchallenge.banking_api.dto.request;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;
/** Input describing a transfer between two accounts. */
public record CreateTransferRequest(
    @NotNull UUID sourceAccountId,
    @NotNull UUID destinationAccountId,
    @NotNull @DecimalMin(value = "0.01") @Digits(integer = 17, fraction = 2) BigDecimal amount,
    @Size(max = 255) String description
) {}
