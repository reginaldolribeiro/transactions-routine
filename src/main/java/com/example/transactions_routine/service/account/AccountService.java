package com.example.transactions_routine.service.account;

import com.example.transactions_routine.controller.account.AccountRequest;
import com.example.transactions_routine.controller.account.TransferRequest;
import com.example.transactions_routine.controller.account.TransferResult;
import com.example.transactions_routine.model.Account;
import com.example.transactions_routine.model.Transaction;
import com.example.transactions_routine.repository.AccountRepository;
import com.example.transactions_routine.repository.OperationTypeRepository;
import com.example.transactions_routine.repository.TransactionRepository;
import com.example.transactions_routine.service.transaction.OperationTypeNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AccountService implements AccountServicePort {

    private final AccountRepository accountRepository;
    private final OperationTypeRepository operationTypeRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, OperationTypeRepository operationTypeRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.operationTypeRepository = operationTypeRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public Account createAccount(AccountRequest accountRequest) {
        try {
            var account = Account.builder()
                    .documentNumber(accountRequest.documentNumber())
                    .build();
            return accountRepository.save(account);
        } catch (DataIntegrityViolationException e) {
            // Check if the error is related to duplicate document number
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("document_number")) {
                throw new AccountDocumentAlreadyExistsException("An account with document number '" + accountRequest.documentNumber() + "' already exists");
            }
            // For other constraint violations, re-throw with more generic message
            throw new IllegalStateException("Failed to create account due to data integrity violation: " + e.getMessage(), e);
        }

    }

    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
    }

    @Override
    @Transactional
    public TransferResult transfer(TransferRequest transferRequest) {
        validateTransferRequest(transferRequest.sourceAccountId(),
                transferRequest.destinationAccountId(),
                transferRequest.amount());

        var sourceAccount = findById(transferRequest.sourceAccountId());
        var destinationAccount = findById(transferRequest.destinationAccountId());
        var transferTime = LocalDateTime.now();

        var debitOperationType = operationTypeRepository.findByDescription("TRANSFER_OUT")
                .orElseThrow(() -> new OperationTypeNotFoundException("Operation type not found with description: TRANSFER_OUT"));
        
        var creditOperationType = operationTypeRepository.findByDescription("TRANSFER_IN")
                .orElseThrow(() -> new OperationTypeNotFoundException("Operation type not found with description: TRANSFER_IN"));

        var debitTransaction = Transaction.builder()
                .account(sourceAccount)
                .operationType(debitOperationType)
                .amount(transferRequest.amount().negate())
                .eventDate(transferTime)
                .build();

        var creditTransaction = Transaction.builder()
                .account(destinationAccount)
                .operationType(creditOperationType)
                .amount(transferRequest.amount())
                .eventDate(transferTime)
                .build();

        transactionRepository.saveAll(List.of(debitTransaction, creditTransaction));

        return new TransferResult(transferTime, debitTransaction, creditTransaction);
    }

    private void validateTransferRequest(Long sourceAccountId, Long destinationAccountId, BigDecimal amount) {
        if (sourceAccountId == null || destinationAccountId == null) {
            throw new InvalidAccountIdException("Account IDs cannot be null");
        }
        if (sourceAccountId.equals(destinationAccountId)) {
            throw new SameAccountTransferException("Cannot transfer to the same account");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferAmountException("Transfer amount must be positive");
        }
    }
}