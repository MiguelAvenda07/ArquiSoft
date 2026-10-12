package com.udea.lab1arq.service;

import com.udea.lab1arq.DTO.TransactionDTO;
import com.udea.lab1arq.entity.Customer;
import com.udea.lab1arq.entity.Transaction;
import com.udea.lab1arq.repository.CustomerRepository;
import com.udea.lab1arq.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService")
class TransactionServiceTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 11, 10, 30, 0);

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Customer sender;
    private Customer receiver;

    @BeforeEach
    void setUp() {
        sender = new Customer(1L, "1001", "Ana", "Gomez", 500.0);
        receiver = new Customer(2L, "1002", "Luis", "Perez", 100.0);
    }

    private TransactionDTO request(String from, String to, Double amount) {
        return new TransactionDTO(null, from, to, amount, NOW);
    }

    @Nested
    @DisplayName("transferMoney")
    class TransferMoney {

        @Test
        @DisplayName("transferencia exitosa: actualiza saldos, guarda la transaccion y devuelve el DTO")
        void success() {
            when(customerRepository.findByAccountNumber("1001")).thenReturn(Optional.of(sender));
            when(customerRepository.findByAccountNumber("1002")).thenReturn(Optional.of(receiver));
            when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> {
                Transaction t = inv.getArgument(0);
                t.setId(100L);
                return t;
            });

            TransactionDTO result = transactionService.transferMoney(request("1001", "1002", 150.0));

            // Saldos
            assertThat(sender.getBalance()).isEqualTo(350.0);
            assertThat(receiver.getBalance()).isEqualTo(250.0);

            // Respuesta
            assertThat(result.getId()).isEqualTo(100L);
            assertThat(result.getSenderAccountNumber()).isEqualTo("1001");
            assertThat(result.getReceiverAccountNumber()).isEqualTo("1002");
            assertThat(result.getAmount()).isEqualTo(150.0);
            assertThat(result.getTimestamp()).isEqualTo(NOW);

            // Persistencia de ambas cuentas y de la transaccion
            verify(customerRepository).save(sender);
            verify(customerRepository).save(receiver);

            ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
            verify(transactionRepository).save(captor.capture());
            Transaction saved = captor.getValue();
            assertThat(saved.getSenderAccountNumber()).isEqualTo("1001");
            assertThat(saved.getReceiverAccountNumber()).isEqualTo("1002");
            assertThat(saved.getAmount()).isEqualTo(150.0);
            assertThat(saved.getTimestamp()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("borde: puede transferir exactamente todo el saldo (queda en 0)")
        void exactBalance_isAllowed() {
            when(customerRepository.findByAccountNumber("1001")).thenReturn(Optional.of(sender));
            when(customerRepository.findByAccountNumber("1002")).thenReturn(Optional.of(receiver));
            when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

            transactionService.transferMoney(request("1001", "1002", 500.0));

            assertThat(sender.getBalance()).isEqualTo(0.0);
            assertThat(receiver.getBalance()).isEqualTo(600.0);
        }

        @Test
        @DisplayName("falla si el numero de cuenta del remitente es nulo")
        void nullSender_throws() {
            assertThatThrownBy(() -> transactionService.transferMoney(request(null, "1002", 10.0)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("obligatorios");

            verifyNoInteractions(customerRepository, transactionRepository);
        }

        @Test
        @DisplayName("falla si el numero de cuenta del receptor es nulo")
        void nullReceiver_throws() {
            assertThatThrownBy(() -> transactionService.transferMoney(request("1001", null, 10.0)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("obligatorios");

            verifyNoInteractions(customerRepository, transactionRepository);
        }

        @Test
        @DisplayName("falla si la cuenta del remitente no existe")
        void senderNotFound_throws() {
            when(customerRepository.findByAccountNumber("9999")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> transactionService.transferMoney(request("9999", "1002", 10.0)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("La cuenta del remitente no existe.");

            verify(customerRepository, never()).save(any(Customer.class));
            verifyNoInteractions(transactionRepository);
        }

        @Test
        @DisplayName("falla si la cuenta del receptor no existe")
        void receiverNotFound_throws() {
            when(customerRepository.findByAccountNumber("1001")).thenReturn(Optional.of(sender));
            when(customerRepository.findByAccountNumber("9999")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> transactionService.transferMoney(request("1001", "9999", 10.0)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("La cuenta del receptor no existe.");

            assertThat(sender.getBalance()).isEqualTo(500.0);
            verify(customerRepository, never()).save(any(Customer.class));
            verifyNoInteractions(transactionRepository);
        }

        @Test
        @DisplayName("falla con saldo insuficiente y no modifica saldos ni persiste nada")
        void insufficientBalance_throws() {
            when(customerRepository.findByAccountNumber("1001")).thenReturn(Optional.of(sender));
            when(customerRepository.findByAccountNumber("1002")).thenReturn(Optional.of(receiver));

            assertThatThrownBy(() -> transactionService.transferMoney(request("1001", "1002", 500.01)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Saldo insuficiente");

            assertThat(sender.getBalance()).isEqualTo(500.0);
            assertThat(receiver.getBalance()).isEqualTo(100.0);
            verify(customerRepository, never()).save(any(Customer.class));
            verifyNoInteractions(transactionRepository);
        }
    }

    @Nested
    @DisplayName("getTransactionsForAccount")
    class GetTransactionsForAccount {

        @Test
        @DisplayName("devuelve las transacciones de la cuenta mapeadas a DTO")
        void returnsMappedTransactions() {
            Transaction t1 = new Transaction(1L, "1001", "1002", 50.0, NOW);
            Transaction t2 = new Transaction(2L, "1003", "1001", 20.0, NOW.plusHours(1));
            when(transactionRepository.findBySenderAccountNumberOrReceiverAccountNumber("1001", "1001"))
                    .thenReturn(List.of(t1, t2));

            List<TransactionDTO> result = transactionService.getTransactionsForAccount("1001");

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(0).getSenderAccountNumber()).isEqualTo("1001");
            assertThat(result.get(0).getReceiverAccountNumber()).isEqualTo("1002");
            assertThat(result.get(0).getAmount()).isEqualTo(50.0);
            assertThat(result.get(0).getTimestamp()).isEqualTo(NOW);
            assertThat(result.get(1).getId()).isEqualTo(2L);
            assertThat(result.get(1).getTimestamp()).isEqualTo(NOW.plusHours(1));
        }

        @Test
        @DisplayName("consulta por la misma cuenta como remitente y como receptor")
        void queriesBySenderOrReceiver() {
            when(transactionRepository.findBySenderAccountNumberOrReceiverAccountNumber("1001", "1001"))
                    .thenReturn(List.of());

            transactionService.getTransactionsForAccount("1001");

            verify(transactionRepository)
                    .findBySenderAccountNumberOrReceiverAccountNumber("1001", "1001");
        }

        @Test
        @DisplayName("devuelve lista vacia si la cuenta no tiene movimientos")
        void noTransactions_returnsEmptyList() {
            when(transactionRepository.findBySenderAccountNumberOrReceiverAccountNumber("2000", "2000"))
                    .thenReturn(List.of());

            assertThat(transactionService.getTransactionsForAccount("2000")).isEmpty();
        }
    }
}