package com.example.transactions_routine.service.account;

import com.example.transactions_routine.fixture.AccountFixture;
import com.example.transactions_routine.model.Account;
import com.example.transactions_routine.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

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

}