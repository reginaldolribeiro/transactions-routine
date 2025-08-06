package com.example.transactions_routine.controller.account;

import com.example.transactions_routine.configuration.JacksonConfig;
import com.example.transactions_routine.fixture.AccountFixture;
import com.example.transactions_routine.model.Account;
import com.example.transactions_routine.service.account.AccountDocumentAlreadyExistsException;
import com.example.transactions_routine.service.account.AccountNotFoundException;
import com.example.transactions_routine.service.account.AccountServicePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import(JacksonConfig.class)
class AccountControllerTest {

    private static final String ACCOUNT_URI = "/v1/accounts";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountServicePort accountServicePort;

    @Nested
    @DisplayName("POST /v1/accounts")
    class createAccount {

        @Test
        @DisplayName("Given a document number it should create an account and return 201 Created with the account details")
        void shouldCreateAccountSuccessfully() throws Exception {
            var expectedDocumentNumber = "34642184813";
            var request = AccountFixture.withValidPayload(expectedDocumentNumber);
            var expectedAccountId = 1L;
            var mockAccount = Account.builder()
                    .id(expectedAccountId)
                    .documentNumber(expectedDocumentNumber)
                    .build();

            when(accountServicePort.createAccount(any(AccountRequest.class))).thenReturn(mockAccount);

            mockMvc.perform(post(ACCOUNT_URI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request)
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                    .andExpect(jsonPath("$.message").value("Account created successfully."))
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.data.id").isNumber())
                    .andExpect(jsonPath("$.data.document_number").value(expectedDocumentNumber))
                    .andExpect(header().string("Location", endsWith(ACCOUNT_URI + "/" + expectedAccountId)));

            verify(accountServicePort, times(1)).createAccount(any(AccountRequest.class));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        @DisplayName("Given a null, empty or blank document number it should NOT create an account and return 400 BAD REQUEST error")
        void shouldNotCreateAccountForInvalidDocumentNumber(String invalidDocumentNumber) throws Exception {
            // Given - Invalid payload with invalid document number
            var invalidRequest = String.format("""
                    {
                      "document_number": %s
                    }
                    """, invalidDocumentNumber == null ? "null" : "\"" + invalidDocumentNumber + "\"");

            // When & Then
            mockMvc.perform(post(ACCOUNT_URI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidRequest)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.message").value("Validation Failed"))
                    .andExpect(jsonPath("$.errors").exists());

            verify(accountServicePort, never()).createAccount(any(AccountRequest.class));
        }

        @Test
                @DisplayName("Given a malformed JSON payload it should return 400 BAD REQUEST error")
        void shouldReturnBadRequestForMalformedJsonPayload() throws Exception {
            // Given
            var malformedJson = AccountFixture.withMalformedJsonPayload();

            // When & Then
            mockMvc.perform(post(ACCOUNT_URI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(malformedJson)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.message").value("Malformed request"));

            verify(accountServicePort, never()).createAccount(any(AccountRequest.class));
        }

        @Test
        @DisplayName("Given an existent document number it should NOT create an account and return 409 CONFLICT error")
        void shouldNotCreateAccountForExistentDocumentNumber() throws Exception {
            // Given
            var documentNumber = "34642184813";
            var request = AccountFixture.withValidPayload(documentNumber);

            when(accountServicePort.createAccount(any(AccountRequest.class)))
                    .thenThrow(new AccountDocumentAlreadyExistsException("An account with document number '" + documentNumber + "' already exists"));

            // When & Then
            mockMvc.perform(post(ACCOUNT_URI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request)
                    )
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.errors").exists());

            verify(accountServicePort, times(1)).createAccount(any(AccountRequest.class));
        }

        @Test
        @DisplayName("Given missing Content-Type header it should return 415 UNSUPPORTED MEDIA TYPE error")
        void shouldReturnUnsupportedMediaTypeForMissingContentType() throws Exception {
            // Given
            var validRequest = AccountFixture.withValidPayload("34642184813");

            // When & Then - No Content-Type header
            mockMvc.perform(post(ACCOUNT_URI)
                            .content(validRequest)
                    )
                    .andExpect(status().isUnsupportedMediaType());

            verify(accountServicePort, never()).createAccount(any(AccountRequest.class));
        }

        @Test
        @DisplayName("Given wrong Content-Type header it should return 415 UNSUPPORTED MEDIA TYPE error")
        void shouldReturnUnsupportedMediaTypeForWrongContentType() throws Exception {
            // Given
            var validRequest = AccountFixture.withValidPayload("34642184813");

            // When & Then - Wrong Content-Type
            mockMvc.perform(post(ACCOUNT_URI)
                            .contentType(MediaType.TEXT_PLAIN)
                            .content(validRequest)
                    )
                    .andExpect(status().isUnsupportedMediaType());

            verify(accountServicePort, never()).createAccount(any(AccountRequest.class));
        }

    }

    @Nested
    @DisplayName("GET /v1/accounts/{id}")
    class findByAccount {

        @Test
        @DisplayName("Given a valid account ID it should return 200 OK with the account details")
        void shouldRetrieveAccountSuccessfully() throws Exception {
            // Given
            var accountId = 1L;
            var documentNumber = "34642184813";
            var mockAccount = AccountFixture.validAccount(accountId, documentNumber);

            when(accountServicePort.findById(accountId)).thenReturn(mockAccount);

            // When & Then
            mockMvc.perform(get(ACCOUNT_URI + "/" + accountId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value("Account found successfully."))
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.data.id").value(accountId))
                    .andExpect(jsonPath("$.data.document_number").value(documentNumber));

            verify(accountServicePort, times(1)).findById(accountId);
        }

        @Test
        @DisplayName("Given an invalid account ID it should return 404 NOT FOUND with the error details")
        void shouldReturn404NotFoundWhenInvalidAccountIdIsProvided() throws Exception {
            // Given
            var invalidAccountId = 999L;
            var expectedMessage = "Account not found with id: " + invalidAccountId;

            when(accountServicePort.findById(invalidAccountId))
                    .thenThrow(new AccountNotFoundException(expectedMessage));

            // When & Then
            mockMvc.perform(get(ACCOUNT_URI + "/" + invalidAccountId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value(expectedMessage))
                    .andExpect(jsonPath("$.errors").exists())
                    .andExpect(jsonPath("$.errors.message").value(expectedMessage))
                    .andExpect(jsonPath("$.timestamp").exists());

            verify(accountServicePort, times(1)).findById(invalidAccountId);
        }

    }


}