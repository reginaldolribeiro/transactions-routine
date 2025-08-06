
package com.example.transactions_routine.controller.account;

import com.example.transactions_routine.model.Account;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Response payload containing the full details of an account.")
public record AccountResponse(
        @Schema(description = "Unique identifier of the account", example = "1")
        Long id,
        @Schema(description = "Document number for the account", example = "12345678900")
        String documentNumber
) {
    public static AccountResponse fromDomain(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getDocumentNumber()
        );
    }
}
