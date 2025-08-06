package com.example.transactions_routine.controller.transaction;

import com.example.transactions_routine.controller.ApiErrorResponse;
import com.example.transactions_routine.controller.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Transactions", description = "Endpoints for creating and retrieving financial transactions.")
public interface TransactionApiDocs {

    @Operation(
            summary = "Create a new transaction",
            description = "Registers a new financial transaction for a specified account and operation type."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Transaction created successfully.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Successful transaction creation",
                                    value = "{\"status\": 201, \"message\": \"Transaction created successfully.\", \"data\": {\"id\": 1, \"account_id\": 1, \"operation_type_id\": 1, \"amount\": -50.00, \"event_date\": \"2023-01-01T12:00:00Z\"}}"
                            ))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request - The request is malformed, contains invalid fields, or fails validation.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Invalid transaction request",
                                    value = "{\"status\": 400, \"message\": \"Validation Failed\", \"errors\": {\"account_id\": \"Account ID is required\"}, \"timestamp\": \"2023-01-01T12:00:00Z\"}"
                            ))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not Found - The specified Account ID or Operation Type ID does not exist.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Account not found",
                                            value = "{\"status\": 404, \"message\": \"Account not found with id: 999\", \"errors\": {\"message\": \"Account not found with id: 999\"}, \"timestamp\": \"2023-01-01T12:00:00Z\"}"
                                    ),
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Operation type not found",
                                            value = "{\"status\": 404, \"message\": \"Operation type not found with id: 5\", \"errors\": {\"message\": \"Operation type not found with id: 5\"}, \"timestamp\": \"2025-08-05T19:04:54.319305\"}"
                                    )
                            }))
    })
    ResponseEntity<ApiResponse<TransactionResponse>> save(@Valid @RequestBody TransactionRequest transactionRequest);

    @Operation(
            summary = "Retrieve a transaction by ID",
            description = "Fetches the details of a single transaction by its unique identifier."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transaction retrieved successfully.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Successful transaction retrieval",
                                    value = "{\"status\": 200, \"message\": \"Transaction found successfully.\", \"data\": {\"id\": 1, \"account_id\": 1, \"operation_type_id\": 1, \"amount\": -50, \"event_date\": \"2020-01-01T10:32:07.719922\"}}"
                            ))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Transaction not found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Transaction not found",
                                    value = "{\"status\": 404, \"message\": \"Transaction not found with id: 999\", \"errors\": {\"message\": \"Transaction not found with id: 999\"}, \"timestamp\": \"2023-01-01T12:00:00Z\"}"
                            )))
    })
    ResponseEntity<ApiResponse<TransactionResponse>> findById(
            @Parameter(
                    description = "Unique identifier of the transaction.",
                    example = "1",
                    required = true
            ) @PathVariable Long id
    );
}
