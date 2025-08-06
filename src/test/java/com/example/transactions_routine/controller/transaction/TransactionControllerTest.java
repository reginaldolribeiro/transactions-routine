package com.example.transactions_routine.controller.transaction;

import com.example.transactions_routine.configuration.JacksonConfig;
import com.example.transactions_routine.fixture.TransactionFixture;
import com.example.transactions_routine.service.transaction.TransactionNotFoundException;
import com.example.transactions_routine.service.transaction.TransactionServicePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@Import(JacksonConfig.class)
class TransactionControllerTest {

    private static final String TRANSACTION_URI = "/v1/transactions";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionServicePort transactionServicePort;

    @Nested
    @DisplayName("POST /v1/transactions")
    class CreateTransaction {

        @Test
        @DisplayName("Given a valid transaction request, it should create the transaction and return 201 Created")
        void shouldCreateTransactionSuccessfully() throws Exception {
            var request = TransactionFixture.withValidPayload();
            var expectedTransaction = TransactionFixture.validTransaction();

            when(transactionServicePort.createTransaction(any(TransactionRequest.class))).thenReturn(expectedTransaction);

            mockMvc.perform(post(TRANSACTION_URI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request)
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                    .andExpect(jsonPath("$.message").value("Transaction created successfully."))
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.data.id").isNumber())
                    .andExpect(jsonPath("$.data.account_id").value(expectedTransaction.getAccount().getId()))
                    .andExpect(jsonPath("$.data.operation_type_id").value(expectedTransaction.getOperationType().getId()))
                    .andExpect(jsonPath("$.data.amount").value(expectedTransaction.getAmount()));

            verify(transactionServicePort, times(1)).createTransaction(any(TransactionRequest.class));
        }

        @Test
        @DisplayName("Given a malformed JSON payload, it should return a 400 BAD REQUEST error")
        void shouldReturnBadRequestForMalformedJsonPayload() throws Exception {
            var malformedJson = TransactionFixture.withMalformedJsonPayload();

            mockMvc.perform(post(TRANSACTION_URI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(malformedJson)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.message").value("Malformed request"))
                    .andExpect(jsonPath("$.errors").exists());

            verify(transactionServicePort, never()).createTransaction(any(TransactionRequest.class));
        }

        @Test
        @DisplayName("Given missing fields in the request, it should return a 400 BAD REQUEST error")
        void shouldReturnBadRequestForMissingFields() throws Exception {
            var missingFieldsJson = """
                    {
                      "operation_type_id": 1,
                      "amount": 123.45
                    }
                    """;

            mockMvc.perform(post(TRANSACTION_URI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(missingFieldsJson)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.message").value("Validation Failed"))
                    .andExpect(jsonPath("$.errors").exists())
                    .andExpect(jsonPath("$.errors.account_id").value("Account ID is required"));

            verify(transactionServicePort, never()).createTransaction(any(TransactionRequest.class));
        }

        @Test
        @DisplayName("Given an invalid data type for fields, it should return a 400 BAD REQUEST error")
        void shouldReturnBadRequestForInvalidDataType() throws Exception {
            var invalidDataTypeJson = TransactionFixture.withInvalidDataTypePayload();

            mockMvc.perform(post(TRANSACTION_URI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidDataTypeJson)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.errors").exists());

            verify(transactionServicePort, never()).createTransaction(any(TransactionRequest.class));
        }

        @Test
        @DisplayName("Given a negative amount, it should return a 400 BAD REQUEST error")
        void shouldReturnBadRequestForNegativeAmount() throws Exception {
            var negativeAmountJson = TransactionFixture.withNegativeAmountPayload();

            mockMvc.perform(post(TRANSACTION_URI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(negativeAmountJson)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.message").value("Validation Failed"))
                    .andExpect(jsonPath("$.errors").exists())
                    .andExpect(jsonPath("$.errors.amount").value("Amount must be positive"));

            verify(transactionServicePort, never()).createTransaction(any(TransactionRequest.class));
        }

        @Test
        @DisplayName("Given a zero amount, it should return a 400 BAD REQUEST error")
        void shouldReturnBadRequestForZeroAmount() throws Exception {
            var zeroAmountJson = TransactionFixture.withZeroAmountPayload();

            mockMvc.perform(post(TRANSACTION_URI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(zeroAmountJson)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.message").value("Validation Failed"))
                    .andExpect(jsonPath("$.errors").exists())
                    .andExpect(jsonPath("$.errors.amount").value("Amount must be positive"));

            verify(transactionServicePort, never()).createTransaction(any(TransactionRequest.class));
        }
    }

    @Nested
    @DisplayName("GET /v1/transactions/{id}")
    class FindTransaction {

        @Test
        @DisplayName("Given a valid transaction ID, it should return 200 OK with the transaction details")
        void shouldRetrieveTransactionSuccessfully() throws Exception {
            var transactionId = 1L;
            var expectedTransaction = TransactionFixture.validTransaction();

            when(transactionServicePort.findById(transactionId)).thenReturn(expectedTransaction);

            mockMvc.perform(get(TRANSACTION_URI + "/" + transactionId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value("Transaction found successfully."))
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.data.id").value(transactionId))
                    .andExpect(jsonPath("$.data.account_id").value(expectedTransaction.getAccount().getId()))
                    .andExpect(jsonPath("$.data.operation_type_id").value(expectedTransaction.getOperationType().getId()))
                    .andExpect(jsonPath("$.data.amount").value(expectedTransaction.getAmount()))
                    .andExpect(jsonPath("$.data.event_date").isNotEmpty());

            verify(transactionServicePort, times(1)).findById(transactionId);
        }

        @Test
        @DisplayName("Given an invalid transaction ID, it should return 404 NOT FOUND")
        void shouldReturn404ForInvalidTransactionId() throws Exception {
            var invalidTransactionId = 999L;

            when(transactionServicePort.findById(invalidTransactionId))
                    .thenThrow(new TransactionNotFoundException("Transaction not found with id: " + invalidTransactionId));

            mockMvc.perform(get(TRANSACTION_URI + "/" + invalidTransactionId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.errors").exists());

            verify(transactionServicePort, times(1)).findById(invalidTransactionId);
        }
    }
}
