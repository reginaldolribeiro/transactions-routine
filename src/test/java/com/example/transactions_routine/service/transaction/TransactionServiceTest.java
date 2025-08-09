package com.example.transactions_routine.service.transaction;

import com.example.transactions_routine.fixture.AccountFixture;
import com.example.transactions_routine.fixture.OperationTypeFixture;
import com.example.transactions_routine.fixture.TransactionFixture;
import com.example.transactions_routine.model.Account;
import com.example.transactions_routine.model.OperationType;
import com.example.transactions_routine.model.Transaction;
import com.example.transactions_routine.repository.AccountRepository;
import com.example.transactions_routine.repository.OperationTypeRepository;
import com.example.transactions_routine.repository.TransactionRepository;
import com.example.transactions_routine.service.account.AccountNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private OperationTypeRepository operationTypeRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Nested
    @DisplayName("Create Transaction")
    class CreateTransaction {

        private final Long mockAccountId = 1L;
        private final Account mockAccount = AccountFixture.validAccount(mockAccountId);
        private final OperationType mockOperationTypeCredit = OperationTypeFixture.validCreditOperationType();
        private final OperationType mockOperationTypeDebit = OperationTypeFixture.validDebitOperationType();

        @Test
        @DisplayName("Given a valid credit operation request, it should create transaction with positive amount")
        void shouldCreateTransactionWithPositiveAmountForCreditOperation() {
            // Given
            var transactionId = 1L;
            var request = TransactionFixture.validTransactionRequest(mockAccountId,
                    mockOperationTypeCredit.getId(),
                    TransactionFixture.SAMPLE_AMOUNT);
            var expectedTransaction = TransactionFixture.validTransaction(transactionId,
                    mockAccountId,
                    mockOperationTypeCredit.getId(),
                    TransactionFixture.SAMPLE_AMOUNT);

            when(accountRepository.findById(mockAccountId)).thenReturn(Optional.of(mockAccount));
            when(operationTypeRepository.findById(mockOperationTypeCredit.getId())).thenReturn(Optional.of(mockOperationTypeCredit));
            when(accountRepository.updateBalanceWithCheck(mockAccountId, request.amount())).thenReturn(1);
            when(transactionRepository.save(any(Transaction.class))).thenReturn(expectedTransaction);

            // When
            var createdTransaction = transactionService.createTransaction(request);

            // Then
            assertAll(
                    () -> assertNotNull(createdTransaction),
                    () -> assertEquals(transactionId, createdTransaction.getId()),
                    () -> assertEquals(mockAccountId, createdTransaction.getAccount().getId()),
                    () -> assertEquals(mockOperationTypeCredit.getId(), createdTransaction.getOperationType().getId()),
                    () -> assertEquals(TransactionFixture.SAMPLE_AMOUNT, createdTransaction.getAmount()),
                    () -> assertNotNull(createdTransaction.getEventDate())
            );
            verify(accountRepository, times(1)).findById(mockAccountId);
            verify(operationTypeRepository, times(1)).findById(mockOperationTypeCredit.getId());
            verify(transactionRepository, times(1)).save(any(Transaction.class));
        }

        @Test
        @DisplayName("Given a valid debit operation request, it should create transaction with negative amount")
        void shouldCreateTransactionWithNegativeAmountForDebitOperation() {
            // Given
            var transactionId = 2L;
            var request = TransactionFixture.validTransactionRequest(mockAccountId,
                    mockOperationTypeDebit.getId(),
                    TransactionFixture.SAMPLE_AMOUNT);
            var expectedTransaction = TransactionFixture.validTransaction(transactionId,
                    mockAccountId,
                    mockOperationTypeDebit.getId(),
                    TransactionFixture.SAMPLE_AMOUNT.negate());

            when(accountRepository.findById(mockAccountId)).thenReturn(Optional.of(mockAccount));
            when(operationTypeRepository.findById(mockOperationTypeDebit.getId()))
                    .thenReturn(Optional.of(mockOperationTypeDebit));
            when(accountRepository.updateBalanceWithCheck(mockAccountId, request.amount().negate())).thenReturn(1);
            when(transactionRepository.save(any(Transaction.class))).thenReturn(expectedTransaction);

            // When
            var createdTransaction = transactionService.createTransaction(request);

            // Then
            assertAll(
                    () -> assertNotNull(createdTransaction),
                    () -> assertEquals(transactionId, createdTransaction.getId()),
                    () -> assertEquals(mockAccountId, createdTransaction.getAccount().getId()),
                    () -> assertEquals(mockOperationTypeDebit.getId(), createdTransaction.getOperationType().getId()),
                    () -> assertEquals(TransactionFixture.SAMPLE_AMOUNT.negate(), createdTransaction.getAmount()),
                    () -> assertNotNull(createdTransaction.getEventDate())
            );
            verify(accountRepository, times(1)).findById(mockAccountId);
            verify(operationTypeRepository, times(1)).findById(mockOperationTypeDebit.getId());
            verify(transactionRepository, times(1)).save(any(Transaction.class));
        }

        @Test
        @DisplayName("Given account does not exist, it should throw AccountNotFoundException")
        void shouldThrowAccountNotFoundExceptionWhenAccountDoesNotExist() {
            // Given
            var request = TransactionFixture.validTransactionRequest(mockAccountId,
                    mockOperationTypeCredit.getId(),
                    TransactionFixture.SAMPLE_AMOUNT);

            when(accountRepository.findById(mockAccountId)).thenReturn(Optional.empty());

            // When / Then
            var thrown = assertThrows(AccountNotFoundException.class, () -> {
                transactionService.createTransaction(request);
            });

            assertEquals("Account not found with id: " + mockAccountId, thrown.getMessage());

            verify(accountRepository, times(1)).findById(mockAccountId);
            verify(operationTypeRepository, never()).findById(anyLong());
            verify(transactionRepository, never()).save(any(Transaction.class));
        }

        @Test
        @DisplayName("Given operation type does not exist, it should throw OperationTypeNotFoundException")
        void shouldThrowOperationTypeNotFoundExceptionWhenOperationTypeDoesNotExist() {
            // Given
            var request = TransactionFixture.validTransactionRequest(mockAccountId,
                    mockOperationTypeCredit.getId(),
                    TransactionFixture.SAMPLE_AMOUNT);

            when(accountRepository.findById(mockAccountId)).thenReturn(Optional.of(mockAccount));
            when(operationTypeRepository.findById(mockOperationTypeCredit.getId())).thenReturn(Optional.empty());

            // When / Then
            var thrown = assertThrows(OperationTypeNotFoundException.class, () -> {
                transactionService.createTransaction(request);
            });

            assertEquals("Operation type not found with id: " + mockOperationTypeCredit.getId(), thrown.getMessage());

            verify(accountRepository, times(1)).findById(mockAccountId);
            verify(operationTypeRepository, times(1)).findById(mockOperationTypeCredit.getId());
            verify(transactionRepository, never()).save(any(Transaction.class));
        }

        @Test
        @DisplayName("Given a debit transaction with insufficient funds, it should throw InsufficientFundsException")
        void shouldThrowInsufficientFundsExceptionWhenBalanceIsNegative() {
            // Given
            var request = TransactionFixture.validTransactionRequest(mockAccountId,
                    mockOperationTypeDebit.getId(),
                    TransactionFixture.SAMPLE_AMOUNT);
            var amountToDebit = request.amount().negate();

            when(accountRepository.findById(mockAccountId)).thenReturn(Optional.of(mockAccount));
            when(operationTypeRepository.findById(mockOperationTypeDebit.getId())).thenReturn(Optional.of(mockOperationTypeDebit));
            when(accountRepository.updateBalanceWithCheck(mockAccountId, amountToDebit)).thenReturn(0);

            // When / Then
            var thrown = assertThrows(InsufficientFundsException.class, () -> {
                transactionService.createTransaction(request);
            });

            assertEquals(String.format("Insufficient funds for transaction. Account ID: %d, Requested amount: %s",
                    mockAccountId, amountToDebit), thrown.getMessage());

            verify(accountRepository, times(1)).findById(mockAccountId);
            verify(operationTypeRepository, times(1)).findById(mockOperationTypeDebit.getId());
            verify(accountRepository, times(1)).updateBalanceWithCheck(mockAccountId, amountToDebit);
            verify(transactionRepository, never()).save(any(Transaction.class));
        }
    }
}