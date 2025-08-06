package com.example.transactions_routine.controller.transaction;

import com.example.transactions_routine.model.Transaction;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Response payload containing the full details of a transaction.")
public record TransactionResponse(
        @Schema(description = "Unique identifier of the transaction", example = "1")
        Long id,
        @Schema(description = "Unique identifier of the associated account", example = "1")
        Long accountId,
        @Schema(description = "Unique identifier of the operation type", example = "1")
        Long operationTypeId,
        @Schema(description = "Transaction amount. A negative value indicates a debit, and a positive value indicates a credit.", example = "-123.45")
        BigDecimal amount,
        @Schema(description = "Timestamp of when the transaction was recorded", example = "2023-01-01T12:00:00Z")
        LocalDateTime eventDate
) {
    public static TransactionResponse fromDomain(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAccount().getId(),
                transaction.getOperationType().getId(),
                transaction.getAmount(),
                transaction.getEventDate()
        );
    }
}
