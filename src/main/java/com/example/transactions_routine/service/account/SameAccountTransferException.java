package com.example.transactions_routine.service.account;

public class SameAccountTransferException extends RuntimeException {
    public SameAccountTransferException() {}

    public SameAccountTransferException(String message) {
        super(message);
    }

    public SameAccountTransferException(String message, Throwable cause) {
        super(message, cause);
    }
}
