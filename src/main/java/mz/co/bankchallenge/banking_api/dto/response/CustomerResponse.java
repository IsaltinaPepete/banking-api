package mz.co.bankchallenge.banking_api.dto.response;

import mz.co.bankchallenge.banking_api.enums.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

/** Public representation of customer identity data. */
public record CustomerResponse(
        UUID customerId,
        String fullName,
        String nuit
) {
}
