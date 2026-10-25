package mz.co.bankchallenge.banking_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import mz.co.bankchallenge.banking_api.enums.AccountType;

/** Input required to create a customer and its initial account. */
public record CreateCustomerRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 80, message = "Username must contain between 3 and 80 characters")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must contain at least 8 characters")
        String password,

        @NotBlank(message = "Full name is required")
        @Size(max = 150, message = "Full name must not exceed 150 characters")
        String fullName,

        @NotBlank(message = "NUIT is required")
        @Pattern(regexp = "^[0-9]{9}$", message = "NUIT must contain exactly 9 digits")
        String nuit,

        @NotNull(message = "Account type is required")
        AccountType accountType
) {
}
