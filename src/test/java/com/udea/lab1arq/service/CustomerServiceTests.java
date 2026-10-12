package com.udea.lab1arq.service;

import com.udea.lab1arq.DTO.CustomerDTO;
import com.udea.lab1arq.entity.Customer;
import com.udea.lab1arq.mapper.CustomerMapper;
import com.udea.lab1arq.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
@DisplayName("CustomerService")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    // Customer(id, accountNumber, firstName, lastName, balance)
    private Customer customer(Long id, String account, String first, String last, Double balance) {
        return new Customer(id, account, first, last, balance);
    }

    // CustomerDTO(id, firstName, lastName, accountNumber, balance)
    private CustomerDTO dto(Long id, String account, String first, String last, Double balance) {
        return new CustomerDTO(id, first, last, account, balance);
    }

    // ---------- getAllCustomer ----------

    @Test
    @DisplayName("getAllCustomer: devuelve todos los clientes mapeados a DTO")
    void getAllCustomer_returnsMappedList() {
        Customer c1 = customer(1L, "1001", "Ana", "Gomez", 500.0);
        Customer c2 = customer(2L, "1002", "Luis", "Perez", 750.0);
        CustomerDTO d1 = dto(1L, "1001", "Ana", "Gomez", 500.0);
        CustomerDTO d2 = dto(2L, "1002", "Luis", "Perez", 750.0);

        when(customerRepository.findAll()).thenReturn(List.of(c1, c2));
        when(customerMapper.toDTO(c1)).thenReturn(d1);
        when(customerMapper.toDTO(c2)).thenReturn(d2);

        List<CustomerDTO> result = customerService.getAllCustomer();

        assertThat(result).containsExactly(d1, d2);
        verify(customerRepository).findAll();
    }

    @Test
    @DisplayName("getAllCustomer: lista vacia cuando no hay clientes")
    void getAllCustomer_emptyRepository_returnsEmptyList() {
        when(customerRepository.findAll()).thenReturn(List.of());

        List<CustomerDTO> result = customerService.getAllCustomer();

        assertThat(result).isEmpty();
        verifyNoInteractions(customerMapper);
    }

    // ---------- getCustomerById ----------

    @Test
    @DisplayName("getCustomerById: devuelve el DTO cuando el cliente existe")
    void getCustomerById_found_returnsDto() {
        Customer c = customer(1L, "1001", "Ana", "Gomez", 500.0);
        CustomerDTO d = dto(1L, "1001", "Ana", "Gomez", 500.0);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));
        when(customerMapper.toDTO(c)).thenReturn(d);

        CustomerDTO result = customerService.getCustomerById(1L);

        assertThat(result).isSameAs(d);
    }

    @Test
    @DisplayName("getCustomerById: lanza RuntimeException cuando no existe")
    void getCustomerById_notFound_throws() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Customer not found");

        verifyNoInteractions(customerMapper);
    }

    // ---------- createCustomer ----------

    @Test
    @DisplayName("createCustomer: mapea a entidad, guarda y devuelve el DTO persistido")
    void createCustomer_savesAndReturnsDto() {
        CustomerDTO input = dto(null, "1003", "Maria", "Lopez", 1000.0);
        Customer entityToSave = customer(null, "1003", "Maria", "Lopez", 1000.0);
        Customer savedEntity = customer(3L, "1003", "Maria", "Lopez", 1000.0);
        CustomerDTO output = dto(3L, "1003", "Maria", "Lopez", 1000.0);

        when(customerMapper.toEntity(input)).thenReturn(entityToSave);
        when(customerRepository.save(entityToSave)).thenReturn(savedEntity);
        when(customerMapper.toDTO(savedEntity)).thenReturn(output);

        CustomerDTO result = customerService.createCustomer(input);

        assertThat(result).isSameAs(output);
        assertThat(result.getId()).isEqualTo(3L);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(captor.capture());
        assertThat(captor.getValue().getAccountNumber()).isEqualTo("1003");
        assertThat(captor.getValue().getBalance()).isEqualTo(1000.0);
    }

    @Test
    @DisplayName("createCustomer: si el repositorio falla, propaga la excepcion")
    void createCustomer_repositoryFails_propagatesException() {
        CustomerDTO input = dto(null, "1003", "Maria", "Lopez", 1000.0);
        Customer entity = customer(null, "1003", "Maria", "Lopez", 1000.0);

        when(customerMapper.toEntity(input)).thenReturn(entity);
        when(customerRepository.save(any(Customer.class)))
                .thenThrow(new IllegalStateException("db down"));

        assertThatThrownBy(() -> customerService.createCustomer(input))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("db down");

        verify(customerMapper, never()).toDTO(any(Customer.class));
    }
}