
package com.example.transactions_routine.service.account;

public class AccountDocumentAlreadyExistsException extends RuntimeException {
    public AccountDocumentAlreadyExistsException() {}

    public AccountDocumentAlreadyExistsException(String message) {
        super(message);
    }

    public AccountDocumentAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
