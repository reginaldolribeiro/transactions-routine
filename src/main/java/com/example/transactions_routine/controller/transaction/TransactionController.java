package com.example.transactions_routine.controller.transaction;

import com.example.transactions_routine.controller.ApiResponse;
import com.example.transactions_routine.service.dto.TransactionInput;
import com.example.transactions_routine.service.transaction.TransactionServicePort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/v1/transactions")
@Validated
public class TransactionController implements TransactionApiDocs{

    private final TransactionServicePort transactionServicePort;

    public TransactionController(TransactionServicePort transactionServicePort) {
        this.transactionServicePort = transactionServicePort;
    }

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> save(
            @Valid @RequestBody TransactionRequest transactionRequest,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        
        var transactionInput = new TransactionInput(
                transactionRequest.accountId(),
                transactionRequest.operationTypeId(),
                transactionRequest.amount(),
                UUID.fromString(idempotencyKey)
        );
        
        var savedTransaction = transactionServicePort.createTransaction(transactionInput);
        var transactionResponse = TransactionResponse.fromDomain(savedTransaction);

        var apiResponse = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Transaction created successfully.",
                transactionResponse,
                null
        );

        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(transactionResponse.id())
                .toUri();

        return ResponseEntity.created(location).body(apiResponse);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> findById(@PathVariable Long id) {
        var transaction = transactionServicePort.findById(id);
        var transactionResponse = TransactionResponse.fromDomain(transaction);
        var apiResponse = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Transaction found successfully.",
                transactionResponse,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
}
