package com.example.transactions_routine.service.transaction;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException() {}

    public TransactionNotFoundException(String message) {
        super(message);
    }

    public TransactionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
