package com.udea.lab1arq.controller;

import com.udea.lab1arq.DTO.CustomerDTO;
import com.udea.lab1arq.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@DisplayName("CustomerController")
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    // CustomerDTO(id, firstName, lastName, accountNumber, balance)
    private CustomerDTO dto(Long id, String first, String last, String account, Double balance) {
        return new CustomerDTO(id, first, last, account, balance);
    }

    // ---------- GET /api/customers ----------

    @Test
    @DisplayName("GET /api/customers: 200 y lista de clientes")
    void getAllCustomers_returnsList() throws Exception {
        when(customerService.getAllCustomer()).thenReturn(List.of(
                dto(1L, "Ana", "Gomez", "1001", 500.0),
                dto(2L, "Luis", "Perez", "1002", 750.0)));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Ana"))
                .andExpect(jsonPath("$[0].accountNumber").value("1001"))
                .andExpect(jsonPath("$[1].balance").value(750.0));
    }

    @Test
    @DisplayName("GET /api/customers: 200 y arreglo vacio si no hay clientes")
    void getAllCustomers_empty() throws Exception {
        when(customerService.getAllCustomer()).thenReturn(List.of());

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ---------- GET /api/customers/{id} ----------

    @Test
    @DisplayName("GET /api/customers/{id}: 200 y el cliente solicitado")
    void getCustomerById_found() throws Exception {
        when(customerService.getCustomerById(1L)).thenReturn(dto(1L, "Ana", "Gomez", "1001", 500.0));

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.lastName").value("Gomez"))
                .andExpect(jsonPath("$.balance").value(500.0));
    }

    @Test
    @DisplayName("GET /api/customers/{id}: la excepcion del servicio se propaga (no hay @ControllerAdvice)")
    void getCustomerById_notFound_propagatesException() {
        when(customerService.getCustomerById(99L)).thenThrow(new RuntimeException("Customer not found"));

        assertThatThrownBy(() -> mockMvc.perform(get("/api/customers/99")))
                .hasRootCauseInstanceOf(RuntimeException.class)
                .hasRootCauseMessage("Customer not found");
    }

    @Test
    @DisplayName("GET /api/customers/{id}: 400 si el id no es numerico")
    void getCustomerById_invalidId_returns400() throws Exception {
        mockMvc.perform(get("/api/customers/abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(customerService);
    }

    // ---------- POST /api/customers ----------

    @Test
    @DisplayName("POST /api/customers: crea el cliente y devuelve 200 con el DTO")
    void createCustomer_valid() throws Exception {
        when(customerService.createCustomer(any(CustomerDTO.class)))
                .thenReturn(dto(3L, "Maria", "Lopez", "1003", 1000.0));

        String body = """
                {"firstName":"Maria","lastName":"Lopez","accountNumber":"1003","balance":1000.0}
                """;

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.accountNumber").value("1003"));

        ArgumentCaptor<CustomerDTO> captor = ArgumentCaptor.forClass(CustomerDTO.class);
        verify(customerService).createCustomer(captor.capture());
        assertThat(captor.getValue().getFirstName()).isEqualTo("Maria");
        assertThat(captor.getValue().getAccountNumber()).isEqualTo("1003");
        assertThat(captor.getValue().getBalance()).isEqualTo(1000.0);
    }

    @Test
    @DisplayName("POST /api/customers: balance nulo lanza IllegalArgumentException y no llama al servicio")
    void createCustomer_nullBalance_throws() {
        String body = """
                {"firstName":"Maria","lastName":"Lopez","accountNumber":"1003"}
                """;

        assertThatThrownBy(() -> mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)))
                .hasRootCauseInstanceOf(IllegalArgumentException.class)
                .hasRootCauseMessage("Balance cannot be null");

        verifyNoInteractions(customerService);
    }

    @Test
    @DisplayName("POST /api/customers: 400 si el JSON esta mal formado")
    void createCustomer_malformedJson_returns400() throws Exception {
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{esto no es json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(customerService);
    }
}