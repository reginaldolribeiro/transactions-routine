package com.example.transactions_routine.service.transaction;

public class OperationTypeNotFoundException extends RuntimeException {
    public OperationTypeNotFoundException() {}

    public OperationTypeNotFoundException(String message) {
        super(message);
    }

    public OperationTypeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
