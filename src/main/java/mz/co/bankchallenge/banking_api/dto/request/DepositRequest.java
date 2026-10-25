package mz.co.bankchallenge.banking_api.dto.request;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
/** Input describing an administrative deposit. */
public record DepositRequest(
    @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) BigDecimal amount,
    @Size(max = 255) String description
) {}
