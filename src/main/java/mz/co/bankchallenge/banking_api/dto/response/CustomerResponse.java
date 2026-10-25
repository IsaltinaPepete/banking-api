package mz.co.bankchallenge.banking_api.dto.response;

import mz.co.bankchallenge.banking_api.enums.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

public record CustomerResponse(
        UUID customerId,
        String fullName,
        String nuit
) {
}
