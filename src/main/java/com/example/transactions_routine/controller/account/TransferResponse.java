package com.example.transactions_routine.controller.account;

import com.example.transactions_routine.controller.transaction.TransactionResponse;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TransferResponse(
        LocalDateTime transferDate,
        TransactionResponse debitTransaction,
        TransactionResponse creditTransaction
) {
}
