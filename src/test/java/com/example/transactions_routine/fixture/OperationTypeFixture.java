package com.example.transactions_routine.fixture;

import com.example.transactions_routine.model.OperationType;

import java.time.LocalDateTime;

public class OperationTypeFixture {

    private static final LocalDateTime FIXED_TIMESTAMP = LocalDateTime.of(2025, 1, 15, 10, 30, 45, 123456789);

    public static OperationType validCreditOperationType() {
        return OperationType.builder()
                .id(4L)
                .description("Credit Voucher")
                .credit(true)
                .createdAt(FIXED_TIMESTAMP)
                .updatedAt(FIXED_TIMESTAMP)
                .build();
    }

    public static OperationType validDebitOperationType() {
        return OperationType.builder()
                .id(1L)
                .description("Normal Purchase")
                .credit(false)
                .createdAt(FIXED_TIMESTAMP)
                .updatedAt(FIXED_TIMESTAMP)
                .build();
    }

}
