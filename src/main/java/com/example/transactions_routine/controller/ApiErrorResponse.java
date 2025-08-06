package com.example.transactions_routine.controller;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Standard error response structure for API errors")
public record ApiErrorResponse(
        @Schema(description = "HTTP status code", example = "400")
        int status,
        @Schema(description = "Error message describing what went wrong", example = "Invalid request payload")
        String message,
        @Schema(description = "Map of field-specific error messages for validation errors", example = "{\"account_id\": \"Account ID is required\"}")
        Map<String, String> errors,
        @Schema(description = "Timestamp of when the error occurred", example = "2023-01-01T12:00:00Z")
        LocalDateTime timestamp
) {
    public ApiErrorResponse(int status, String message){
        this(status, message, Map.of("message", message), LocalDateTime.now(Clock.systemUTC()));
    }
    public ApiErrorResponse(int status, String message, Map<String, String> errors){
        this(status, message, errors, LocalDateTime.now(Clock.systemUTC()));
    }
}
