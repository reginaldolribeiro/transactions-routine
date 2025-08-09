package com.example.transactions_routine.service.account;

public class InvalidAccountIdException extends RuntimeException {
    public InvalidAccountIdException() {}

    public InvalidAccountIdException(String message) {
        super(message);
    }

    public InvalidAccountIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
