package com.example.transactions_routine.service.transaction;

import com.example.transactions_routine.model.Transaction;
import com.example.transactions_routine.repository.AccountRepository;
import com.example.transactions_routine.repository.OperationTypeRepository;
import com.example.transactions_routine.repository.TransactionRepository;
import com.example.transactions_routine.service.account.AccountNotFoundException;
import com.example.transactions_routine.service.dto.TransactionInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TransactionService implements TransactionServicePort {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final OperationTypeRepository operationTypeRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              OperationTypeRepository operationTypeRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.operationTypeRepository = operationTypeRepository;
    }

    @Override
    @Transactional
    public Transaction createTransaction(TransactionInput transactionInput) {
        logger.debug("Creating transaction for account {} with idempotency key {}", 
                transactionInput.accountId(), transactionInput.idempotencyKey());
        
        // Optimized idempotency check: lightweight existence check first (common case optimization)
        if (isDuplicateTransaction(transactionInput.accountId(), transactionInput.idempotencyKey())) {
            return transactionRepository.findByAccountIdAndIdempotencyKey(
                    transactionInput.accountId(), 
                    transactionInput.idempotencyKey()
            ).orElseThrow(); // Should never be empty since exists() returned true
        }

        var account = accountRepository.findById(transactionInput.accountId())
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + transactionInput.accountId()));

        var operationType = operationTypeRepository.findById(transactionInput.operationTypeId())
                .orElseThrow(() -> new OperationTypeNotFoundException("Operation type not found with id: " + transactionInput.operationTypeId()));

        // Create transaction with negative amount for debit operations (non-credit operations)
        var amount = operationType.isCredit()
                ? transactionInput.amount()
                : transactionInput.amount().negate();

        var transaction = Transaction.builder()
                .account(account)
                .operationType(operationType)
                .amount(amount)
                .eventDate(LocalDateTime.now())
                .idempotencyKey(transactionInput.idempotencyKey())
                .build();

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction findById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + transactionId));
    }

    private boolean isDuplicateTransaction(Long accountId, UUID idempotencyKey) {
        logger.debug("Checking if transaction already exists...");
        long count = transactionRepository.countByAccountIdAndIdempotencyKey(accountId, idempotencyKey);
        boolean exists = count > 0;
        logger.debug("Transaction count: {}, exists: {}", count, exists);
        return exists;
    }

}
