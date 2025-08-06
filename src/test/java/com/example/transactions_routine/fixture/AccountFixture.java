package com.example.transactions_routine.fixture;

import com.example.transactions_routine.controller.account.AccountRequest;
import com.example.transactions_routine.model.Account;

import java.time.LocalDateTime;

public class AccountFixture {

    public static final String DOCUMENT_NUMBER = "12345678900";

    public static String withValidPayload(String documentNumber) {
        return """
                {
                  "document_number": "%s"
                }                    
                """.trim().formatted(documentNumber);
    }

    public static String withMalformedJsonPayload() {
        return """
                {
                  "document_number": "12345678900
                """.trim();
    }

    public static Account validAccount(Long accountId) {
        return validAccount(accountId, DOCUMENT_NUMBER);
    }

    public static Account validAccount(Long accountId, String documentNumber) {
        var now = LocalDateTime.now();
        return Account.builder()
                .id(accountId)
                .documentNumber(documentNumber)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static AccountRequest validAccountRequest() {
        return new AccountRequest(DOCUMENT_NUMBER);
    }
}
