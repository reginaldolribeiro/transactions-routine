package com.example.transactions_routine.controller;

import com.example.transactions_routine.service.account.AccountDocumentAlreadyExistsException;
import com.example.transactions_routine.service.account.AccountNotFoundException;
import com.example.transactions_routine.service.account.InvalidAccountIdException;
import com.example.transactions_routine.service.account.InvalidTransferAmountException;
import com.example.transactions_routine.service.account.SameAccountTransferException;
import com.example.transactions_routine.service.transaction.InsufficientFundsException;
import com.example.transactions_routine.service.transaction.OperationTypeNotFoundException;
import com.example.transactions_routine.service.transaction.TransactionNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(AccountDocumentAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountDocumentAlreadyExistsException(AccountDocumentAlreadyExistsException ex) {
        logger.warn("AccountDocumentAlreadyExistsException: {}", ex.getMessage());
        var errorResponse = new ApiErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountNotFoundException(AccountNotFoundException ex) {
        logger.warn("AccountNotFoundException: {}", ex.getMessage());
        var errorResponse = new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleTransactionNotFoundException(TransactionNotFoundException ex) {
        logger.warn("TransactionNotFoundException: {}", ex.getMessage());
        var errorResponse = new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(OperationTypeNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleOperationTypeNotFoundException(OperationTypeNotFoundException ex) {
        logger.warn("OperationTypeNotFoundException: {}", ex.getMessage());
        var errorResponse = new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(InvalidTransferAmountException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidTransferAmountException(InvalidTransferAmountException ex) {
        logger.warn("InvalidTransferAmountException: {}", ex.getMessage());
        var errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(SameAccountTransferException.class)
    public ResponseEntity<ApiErrorResponse> handleSameAccountTransferException(SameAccountTransferException ex) {
        logger.warn("SameAccountTransferException: {}", ex.getMessage());
        var errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(InvalidAccountIdException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidAccountIdException(InvalidAccountIdException ex) {
        logger.warn("InvalidAccountIdException: {}", ex.getMessage());
        var errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientFundsException(InsufficientFundsException ex) {
        logger.warn("InsufficientFundsException: {}", ex.getMessage());
        var errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.warn("MethodArgumentNotValidException: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            var fieldName = objectMapper.getPropertyNamingStrategy().nameForField(null, null, error.getField());
            errors.put(fieldName, error.getDefaultMessage());
        });
        var errorResponse = new ApiErrorResponse(ex.getStatusCode().value(), "Validation Failed", errors);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatchExceptions(MethodArgumentTypeMismatchException ex) {
        logger.warn("MethodArgumentTypeMismatchException: Parameter '{}' has invalid value '{}'", ex.getName(), ex.getValue());

        if (ex.getRequiredType() == UUID.class) {
            var error = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(),
                    "Invalid UUID format for parameter: " + ex.getName());
            return ResponseEntity.badRequest().body(error);
        }

        var generic = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Invalid parameter " + ex.getName());
        return ResponseEntity.badRequest().body(generic);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        logger.warn("HttpMediaTypeNotSupportedException: {}", ex.getMessage());
        var errorResponse = new ApiErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                "Content-Type '" + ex.getContentType() + "' is not supported");
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleDeserialization(HttpMessageNotReadableException ex) {
        logger.error("HttpMessageNotReadableException: {}", ex.getMessage(), ex);
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException invalidFormatException) {
            return ResponseEntity.badRequest().body(new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid format for field: " + invalidFormatException.getPathReference()));
        }
        return ResponseEntity.badRequest().body(new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), "Malformed request"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unhandled exception caught: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        var error = new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}
