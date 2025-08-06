package com.example.transactions_routine.service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionInput(
        Long accountId,
        Long operationTypeId,
        BigDecimal amount,
        UUID idempotencyKey
) {
}
