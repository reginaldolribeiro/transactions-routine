package com.example.transactions_routine.service.account;

import com.example.transactions_routine.controller.account.TransferRequest;
import com.example.transactions_routine.fixture.AccountFixture;
import com.example.transactions_routine.model.Account;
import com.example.transactions_routine.model.OperationType;
import com.example.transactions_routine.repository.AccountRepository;
import com.example.transactions_routine.repository.OperationTypeRepository;
import com.example.transactions_routine.repository.TransactionRepository;
import com.example.transactions_routine.service.transaction.InsufficientFundsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private OperationTypeRepository operationTypeRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Nested
    @DisplayName("Create an Account")
    class createAccount {

        @Test
        @DisplayName("Given a valid document number is provided, it should create an account")
        void shouldCreateAccountWhenDocumentNumberIsValid() {
            var accountRequest = AccountFixture.validAccountRequest();
            var expectedAccount = AccountFixture.validAccount(1L, accountRequest.documentNumber());

            when(accountRepository.save(any(Account.class))).thenReturn(expectedAccount);

            var createdAccount = accountService.createAccount(accountRequest);

            assertAll(
                    () -> assertNotNull(createdAccount),
                    () -> assertEquals(expectedAccount.getId(), createdAccount.getId()),
                    () -> assertEquals(expectedAccount.getDocumentNumber(), createdAccount.getDocumentNumber())
            );

            verify(accountRepository, times(1)).save(any(Account.class));
        }


        @Test
        @DisplayName("Given an existent document number is provided, it should not create an account and return error")
        void shouldThrowExceptionWhenDocumentNumberAlreadyExists() {
            var accountRequest = AccountFixture.validAccountRequest();

            when(accountRepository.save(any(Account.class)))
                    .thenThrow(AccountDocumentAlreadyExistsException.class);

            assertThrows(AccountDocumentAlreadyExistsException.class,
                    () -> accountService.createAccount(accountRequest));

            verify(accountRepository, times(1)).save(any(Account.class));
        }

    }

    @Nested
    @DisplayName("Find an Account by ID")
    class findById {

        private final Long accountId = 1L;

        @Test
        @DisplayName("Given a valid ID, it should return the account")
        void shouldReturnAccountWhenIdExists() {
            var expectedAccount = AccountFixture.validAccount(accountId, AccountFixture.DOCUMENT_NUMBER);

            when(accountRepository.findById(accountId)).thenReturn(java.util.Optional.of(expectedAccount));

            var foundAccount = accountService.findById(accountId);

            assertAll(
                    () -> assertNotNull(foundAccount),
                    () -> assertEquals(expectedAccount.getId(), foundAccount.getId()),
                    () -> assertEquals(expectedAccount.getDocumentNumber(), foundAccount.getDocumentNumber())
            );

            verify(accountRepository, times(1)).findById(accountId);
        }

        @Test
        @DisplayName("Given an invalid ID, it should return AccountNotFoundException error")
        void shouldThrowExceptionWhenIdDoesNotExist() {
            when(accountRepository.findById(accountId)).thenReturn(java.util.Optional.empty());

            assertThrows(AccountNotFoundException.class,
                    () -> accountService.findById(accountId));

            verify(accountRepository, times(1)).findById(accountId);
        }

    }

    @Nested
    @DisplayName("Transfer between accounts")
    class Transfer {

        private final Long sourceAccountId = 1L;
        private final Long destinationAccountId = 2L;
        private final BigDecimal transferAmount = new BigDecimal("100.00");

        @Test
        @DisplayName("Given valid accounts and sufficient funds, it should successfully transfer amount")
        void shouldSuccessfullyTransferAmountBetweenAccounts() {
            // Given
            var sourceAccount = AccountFixture.validAccount(sourceAccountId);
            var destinationAccount = AccountFixture.validAccount(destinationAccountId);
            var transferRequest = new TransferRequest(sourceAccountId, destinationAccountId, transferAmount);

            var debitOperationType = OperationType.builder().description("TRANSFER_OUT").credit(false).build();
            var creditOperationType = OperationType.builder().description("TRANSFER_IN").credit(true).build();

            when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
            when(accountRepository.findById(destinationAccountId)).thenReturn(Optional.of(destinationAccount));
            when(operationTypeRepository.findByDescription("TRANSFER_OUT")).thenReturn(Optional.of(debitOperationType));
            when(operationTypeRepository.findByDescription("TRANSFER_IN")).thenReturn(Optional.of(creditOperationType));
            when(accountRepository.updateBalanceWithCheck(sourceAccountId, transferAmount.negate())).thenReturn(1);
            when(accountRepository.updateBalance(destinationAccountId, transferAmount)).thenReturn(1);

            // When
            var transferResult = accountService.transfer(transferRequest);

            // Then
            assertNotNull(transferResult);
            assertNotNull(transferResult.debitTransaction());
            assertNotNull(transferResult.creditTransaction());
            assertEquals(transferAmount.negate(), transferResult.debitTransaction().getAmount());
            assertEquals(transferAmount, transferResult.creditTransaction().getAmount());

            verify(accountRepository, times(2)).findById(anyLong());
            verify(operationTypeRepository, times(2)).findByDescription(anyString());
            verify(accountRepository).updateBalanceWithCheck(sourceAccountId, transferAmount.negate());
            verify(accountRepository).updateBalance(destinationAccountId, transferAmount);
            verify(transactionRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("Given insufficient funds, it should throw InsufficientFundsException")
        void shouldThrowInsufficientFundsExceptionWhenSourceAccountHasNoBalance() {
            // Given
            var sourceAccount = AccountFixture.validAccount(sourceAccountId);
            var destinationAccount = AccountFixture.validAccount(destinationAccountId);
            var transferRequest = new TransferRequest(sourceAccountId, destinationAccountId, transferAmount);
            var debitOperationType = OperationType.builder().description("TRANSFER_OUT").credit(false).build();

            when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
            when(accountRepository.findById(destinationAccountId)).thenReturn(Optional.of(destinationAccount));
            when(operationTypeRepository.findByDescription("TRANSFER_OUT")).thenReturn(Optional.of(debitOperationType));
            when(accountRepository.updateBalanceWithCheck(sourceAccountId, transferAmount.negate())).thenReturn(0);

            // When / Then
            assertThrows(InsufficientFundsException.class, () -> accountService.transfer(transferRequest));

            verify(accountRepository, times(2)).findById(anyLong());
            verify(operationTypeRepository).findByDescription("TRANSFER_OUT");
            verify(accountRepository).updateBalanceWithCheck(sourceAccountId, transferAmount.negate());
            verify(transactionRepository, never()).saveAll(anyList());
        }

        @Test
        @DisplayName("Given same source and destination account, it should throw SameAccountTransferException")
        void shouldThrowSameAccountTransferExceptionWhenSourceAndDestinationAreSame() {
            // Given
            var transferRequest = new TransferRequest(sourceAccountId, sourceAccountId, transferAmount);

            // When / Then
            assertThrows(SameAccountTransferException.class, () -> accountService.transfer(transferRequest));

            verify(accountRepository, never()).findById(anyLong());
        }

        @Test
        @DisplayName("Given a negative transfer amount, it should throw InvalidTransferAmountException")
        void shouldThrowInvalidTransferAmountExceptionForNegativeAmount() {
            // Given
            var transferRequest = new TransferRequest(sourceAccountId, destinationAccountId, new BigDecimal("-50.00"));

            // When / Then
            assertThrows(InvalidTransferAmountException.class, () -> accountService.transfer(transferRequest));

            verify(accountRepository, never()).findById(anyLong());
        }
    }

}
