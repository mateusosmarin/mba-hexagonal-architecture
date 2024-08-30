package br.com.fullcycle.hexagonal.application.domain.customer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

class CustomerTest {
    @Test
    @DisplayName("Deve instanciar um cliente")
    public void testCreate() {
        // given
        final var expectedCPF = "123.456.789-01";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        // when
        final var customer = Customer.newCustomer(expectedName, expectedCPF, expectedEmail);

        // then
        Assertions.assertNotNull(customer.id());
        Assertions.assertEquals(expectedCPF, customer.cpf().value());
        Assertions.assertEquals(expectedEmail, customer.email().value());
        Assertions.assertEquals(expectedName, customer.name().value());
    }

    @Test
    @DisplayName("Não deve instanciar um cliente com CPF inválido")
    public void testCreateInvalidCpf() {
        // given
        final var expectedError = "Invalid value for Cpf";

        // when
        final var actualException = Assertions.assertThrows(ValidationException.class,
                () -> Customer.newCustomer("John Doe", "12345678901", "john.doe@gmail.com"));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve instanciar um cliente com e-mail inválido")
    public void testCreateInvalidEmail() {
        // given
        final var expectedError = "Invalid value for Email";

        // when
        final var actualException = Assertions.assertThrows(ValidationException.class,
                () -> Customer.newCustomer("John Doe", "123.456.789-01", "john.doe@gmail"));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve instanciar um cliente com nome inválido")
    public void testCreateInvalidName() {
        // given
        final var expectedError = "Invalid value for Name";

        // when
        final var actualException = Assertions.assertThrows(ValidationException.class,
                () -> Customer.newCustomer(null, "123.456.789-01", "john.doe@gmail.com"));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }
}
