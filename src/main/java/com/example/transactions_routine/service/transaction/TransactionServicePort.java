package com.example.transactions_routine.service.transaction;

import com.example.transactions_routine.controller.transaction.TransactionRequest;
import com.example.transactions_routine.model.Transaction;

public interface TransactionServicePort {
    Transaction createTransaction(TransactionRequest transactionRequest);
    Transaction findById(Long transactionId);
}
