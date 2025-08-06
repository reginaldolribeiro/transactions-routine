package com.example.transactions_routine.service.transaction;

import com.example.transactions_routine.controller.transaction.TransactionRequest;
import com.example.transactions_routine.model.Transaction;
import com.example.transactions_routine.repository.AccountRepository;
import com.example.transactions_routine.repository.OperationTypeRepository;
import com.example.transactions_routine.repository.TransactionRepository;
import com.example.transactions_routine.service.account.AccountNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class TransactionService implements TransactionServicePort {

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
    public Transaction createTransaction(TransactionRequest transactionRequest) {
        var account = accountRepository.findById(transactionRequest.accountId())
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + transactionRequest.accountId()));

        var operationType = operationTypeRepository.findById(transactionRequest.operationTypeId())
                .orElseThrow(() -> new OperationTypeNotFoundException("Operation type not found with id: " + transactionRequest.operationTypeId()));

        // Create transaction with negative amount for debit operations (non-credit operations)
        var amount = operationType.isCredit()
                ? transactionRequest.amount()
                : transactionRequest.amount().negate();

        var transaction = Transaction.builder()
                .account(account)
                .operationTypeId(operationType)
                .amount(amount)
                .eventDate(LocalDateTime.now())
                .build();

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction findById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + transactionId));
    }

}
