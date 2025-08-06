package com.example.transactions_routine.controller;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard success response structure for API operations")
public record ApiResponse<T>(
        @Schema(description = "HTTP status code", example = "200")
        Integer status,
        @Schema(description = "Success message", example = "Operation completed successfully")
        String message,
        @Schema(description = "Response data payload")
        T data,
        @Schema(description = "Additional metadata (optional)")
        Object metadata
) {
}

