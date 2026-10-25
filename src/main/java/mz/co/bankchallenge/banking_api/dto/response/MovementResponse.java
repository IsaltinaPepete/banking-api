package mz.co.bankchallenge.banking_api.dto.response;
import mz.co.bankchallenge.banking_api.enums.MovementDirection;
import mz.co.bankchallenge.banking_api.enums.TransactionType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
public record MovementResponse(Long id, UUID transactionReference,
                               TransactionType type, MovementDirection direction, BigDecimal amount,
                               BigDecimal balanceAfter, String description, OffsetDateTime createdAt) {}
