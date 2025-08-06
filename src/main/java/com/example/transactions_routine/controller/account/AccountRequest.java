
package com.example.transactions_routine.controller.account;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Request payload for creating a new account")
public record AccountRequest(
        @Schema(description = "Document number for the account", example = "12345678900", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Document number is required")
        String documentNumber
) {
}
