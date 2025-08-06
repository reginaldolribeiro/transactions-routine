package com.example.transactions_routine.controller.transaction;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Request payload for creating a new transaction")
public record TransactionRequest(
        @Schema(description = "Unique identifier of the account", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Account ID is required")
        Long accountId,

        @Schema(description = "Unique identifier of the operation type (1=Normal Purchase, 2=Purchase with installments, 3=Withdrawal, 4=Credit Voucher)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Operation type ID is required")
        Long operationTypeId,

        @Schema(description = "Transaction amount (always positive, sign is determined by operation type)", example = "123.45", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount
) {
}
