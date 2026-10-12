package com.udea.lab1arq.controller;

import com.udea.lab1arq.DTO.TransactionDTO;
import com.udea.lab1arq.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@DisplayName("TransactionController")
class TransactionControllerTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 11, 10, 30, 0);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    private static final String VALID_BODY = """
            {"senderAccountNumber":"1001","receiverAccountNumber":"1002",
             "amount":150.0,"timestamp":"2026-10-11 10:30:00"}
            """;

    // ---------- POST /api/transactions ----------

    @Test
    @DisplayName("POST /api/transactions: 200 y la transaccion creada")
    void transferMoney_success() throws Exception {
        when(transactionService.transferMoney(any(TransactionDTO.class)))
                .thenReturn(new TransactionDTO(100L, "1001", "1002", 150.0, NOW));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.senderAccountNumber").value("1001"))
                .andExpect(jsonPath("$.receiverAccountNumber").value("1002"))
                .andExpect(jsonPath("$.amount").value(150.0))
                .andExpect(jsonPath("$.timestamp").value("2026-10-11 10:30:00"));

        // El JSON de entrada se deserializa correctamente hacia el servicio
        ArgumentCaptor<TransactionDTO> captor = ArgumentCaptor.forClass(TransactionDTO.class);
        verify(transactionService).transferMoney(captor.capture());
        assertThat(captor.getValue().getSenderAccountNumber()).isEqualTo("1001");
        assertThat(captor.getValue().getReceiverAccountNumber()).isEqualTo("1002");
        assertThat(captor.getValue().getAmount()).isEqualTo(150.0);
        assertThat(captor.getValue().getTimestamp()).isEqualTo(NOW);
    }

    @Test
    @DisplayName("POST /api/transactions: 400 con el mensaje cuando el servicio lanza IllegalArgumentException")
    void transferMoney_illegalArgument_returns400WithMessage() throws Exception {
        when(transactionService.transferMoney(any(TransactionDTO.class)))
                .thenThrow(new IllegalArgumentException("Saldo insuficiente en la cuenta del remitente."));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Saldo insuficiente en la cuenta del remitente."));
    }

    @Test
    @DisplayName("POST /api/transactions: 400 si la cuenta del remitente no existe")
    void transferMoney_senderNotFound_returns400() throws Exception {
        when(transactionService.transferMoney(any(TransactionDTO.class)))
                .thenThrow(new IllegalArgumentException("La cuenta del remitente no existe."));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("La cuenta del remitente no existe."));
    }

    @Test
    @DisplayName("POST /api/transactions: 400 si el JSON esta mal formado")
    void transferMoney_malformedJson_returns400() throws Exception {
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{esto no es json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(transactionService);
    }

    // ---------- GET /api/transactions/{accountNumber} ----------

    @Test
    @DisplayName("GET /api/transactions/{account}: 200 y el historial de la cuenta")
    void getTransactionsByAccount_returnsHistory() throws Exception {
        when(transactionService.getTransactionsForAccount("1001")).thenReturn(List.of(
                new TransactionDTO(1L, "1001", "1002", 50.0, NOW),
                new TransactionDTO(2L, "1003", "1001", 20.0, NOW.plusHours(1))));

        mockMvc.perform(get("/api/transactions/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].amount").value(50.0))
                .andExpect(jsonPath("$[0].timestamp").value("2026-10-11 10:30:00"))
                .andExpect(jsonPath("$[1].senderAccountNumber").value("1003"));

        verify(transactionService).getTransactionsForAccount("1001");
    }

    @Test
    @DisplayName("GET /api/transactions/{account}: 200 y arreglo vacio si no hay movimientos")
    void getTransactionsByAccount_empty() throws Exception {
        when(transactionService.getTransactionsForAccount("2000")).thenReturn(List.of());

        mockMvc.perform(get("/api/transactions/2000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}