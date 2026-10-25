package mz.co.bankchallenge.banking_api.dto.response;

import mz.co.bankchallenge.banking_api.enums.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

/** Response returned after creating a customer and its bank account. */
public record CreateCustomerResponse(
        UUID customerId,
        String fullName,
        String nuit,
        UUID accountId,
        String accountNumber,
        AccountType accountType,
        String currency,
        BigDecimal balance
) {
}
