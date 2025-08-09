package com.example.transactions_routine.controller.account;

import com.example.transactions_routine.model.Transaction;

import java.time.LocalDateTime;

public record TransferResult(
        LocalDateTime transferDate,
        Transaction debitTransaction,
        Transaction creditTransaction
) {
}
