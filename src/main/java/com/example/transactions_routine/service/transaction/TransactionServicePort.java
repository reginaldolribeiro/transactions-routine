package com.example.transactions_routine.service.transaction;

import com.example.transactions_routine.model.Transaction;
import com.example.transactions_routine.service.dto.TransactionInput;

public interface TransactionServicePort {
    Transaction createTransaction(TransactionInput transactionInput);
    Transaction findById(Long transactionId);
}
