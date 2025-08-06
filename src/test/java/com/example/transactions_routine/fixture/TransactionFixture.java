package com.example.transactions_routine.fixture;

import com.example.transactions_routine.controller.transaction.TransactionRequest;
import com.example.transactions_routine.model.Account;
import com.example.transactions_routine.model.OperationType;
import com.example.transactions_routine.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionFixture {

    public static final BigDecimal SAMPLE_AMOUNT = new BigDecimal("100.00");

    // Fixed timestamp to avoid precision issues in tests
    private static final LocalDateTime FIXED_TIMESTAMP = LocalDateTime.of(2025, 1, 15, 10, 30, 45, 123456789);

    public static String withValidPayload(Long accountId, Long operationTypeId, BigDecimal amount) {
        return """
                {
                  "account_id": %d,
                  "operation_type_id": %d,
                  "amount": %s
                }                    
                """.trim().formatted(accountId, operationTypeId, amount);
    }

    public static String withMalformedJsonPayload() {
        return """
                {
                  "account_id": 1,
                  "operation_type_id": 1,
                  "amount": 123.45
                """.trim();
    }

    public static String withInvalidDataTypePayload() {
        return """
                {
                  "account_id": "not_a_number",
                  "operation_type_id": 1,
                  "amount": 123.45
                }
                """.trim();
    }

    public static String withNegativeAmountPayload() {
        return """
                {
                  "account_id": 1,
                  "operation_type_id": 1,
                  "amount": -123.45
                }
                """.trim();
    }

    public static String withZeroAmountPayload() {
        return """
                {
                  "account_id": 1,
                  "operation_type_id": 1,
                  "amount": 0
                }
                """.trim();
    }

    public static String withValidPayload() {
        return withValidPayload(1L, 1L, new BigDecimal("123.45"));
    }

    public static Transaction validTransaction(Long transactionId,
                                               Long accountId,
                                               Long operationTypeId,
                                               BigDecimal amount) {
        var account = Account.builder()
                .id(accountId)
                .documentNumber("12345678900")
                .createdAt(FIXED_TIMESTAMP)
                .updatedAt(FIXED_TIMESTAMP)
                .build();

        var operationType = OperationType.builder()
                .id(operationTypeId)
                .description("Normal Purchase")
                .credit(false)
                .createdAt(FIXED_TIMESTAMP)
                .updatedAt(FIXED_TIMESTAMP)
                .build();

        return Transaction.builder()
                .id(transactionId)
                .account(account)
                .operationType(operationType)
                .amount(amount)
                .eventDate(FIXED_TIMESTAMP)
                .createdAt(FIXED_TIMESTAMP)
                .updatedAt(FIXED_TIMESTAMP)
                .build();
    }

    public static Transaction validTransaction() {
        return validTransaction(1L,
                1L,
                1L,
                new BigDecimal("123.45"));
    }

    public static TransactionRequest validTransactionRequest(Long accountId,
                                                             Long operationTypeId,
                                                             BigDecimal amount) {
        return new TransactionRequest(accountId, operationTypeId, amount);
    }

}
