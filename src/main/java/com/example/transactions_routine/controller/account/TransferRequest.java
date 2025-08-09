package com.example.transactions_routine.controller.account;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TransferRequest(
        @NotNull(message = "Source Account ID is required")
        Long sourceAccountId,

        @NotNull(message = "Destination Account ID is required")
        Long destinationAccountId,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount
) {
}
