package com.example.transactions_routine.controller.account;

import com.example.transactions_routine.controller.ApiErrorResponse;
import com.example.transactions_routine.controller.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Account", description = "Endpoints for managing accounts")
public interface AccountApiDocs {

    @Operation(
            summary = "Create a new account",
            description = "Creates a new account with the provided document number",
            requestBody = @RequestBody(
                    description = "Account details",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AccountRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Valid Request",
                                            value = "{\"document_number\": \"12345678900\"}"
                                    )
                            }
                    )
            )
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Account created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Response",
                                            value = "{\"status\":201,\"message\":\"Account created successfully\",\"data\":{\"id\":1,\"document_number\":\"12345678900\"}}"
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Blank Document Number",
                                            value = "{\"status\":400,\"message\":\"Validation Failed\",\"errors\":{\"document_number\":\"Document number is required\"},\"timestamp\":\"2025-08-05T19:12:53.189176\"}"
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Account with the given document number already exists",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Duplicate Account",
                                            value = "{\"status\":409,\"message\":\"Account with document number 12345678900 already exists\",\"errors\":{\"message\":\"Account with document number 12345678900 already exists\"},\"timestamp\":\"2025-08-05T19:12:53.189176\"}"
                                    )
                            }
                    )
            )
    })
    ResponseEntity<ApiResponse<AccountResponse>> save(@Valid @RequestBody AccountRequest accountRequest);

    @Operation(
            summary = "Find account by ID",
            description = "Returns a single account, if found"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Account found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Response",
                                            value = "{\"status\":200,\"message\":\"Account found successfully\",\"data\":{\"id\":1,\"document_number\":\"12345678900\"}}"
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Account not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Account Not Found",
                                            value = "{\"status\":404,\"message\":\"Account not found with id: 1\",\"errors\":{\"message\":\"Account not found with id: 1\"},\"timestamp\":\"2025-08-05T19:12:53.189176\"}"
                                    )
                            }
                    )
            )
    })
    ResponseEntity<ApiResponse<AccountResponse>> findById(
            @Parameter(
                    description = "Unique identifier of the account.",
                    example = "1",
                    required = true
            ) @PathVariable Long id);
}

