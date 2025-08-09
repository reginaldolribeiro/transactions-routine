
package com.example.transactions_routine.service.account;

public class InvalidTransferAmountException extends RuntimeException {
    public InvalidTransferAmountException() {}

    public InvalidTransferAmountException(String message) {
        super(message);
    }

    public InvalidTransferAmountException(String message, Throwable cause) {
        super(message, cause);
    }
}
