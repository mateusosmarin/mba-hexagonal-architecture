package br.com.fullcycle.hexagonal.application.domain.person;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

class CpfTest {
    @Test
    @DisplayName("Deve instanciar um CPF válido")
    void testValidCpf() {
        // given
        final var expectedCpf = "123.456.789-00";

        // when
        final var actualCpf = new Cpf(expectedCpf);

        // then
        Assertions.assertEquals(expectedCpf, actualCpf.value());
    }

    @Test
    @DisplayName("Não deve instanciar um CPF inválido")
    void testInvalidCpf() {
        // given
        final var expectedError = "Invalid value for Cpf";

        // when
        final var actualException = Assertions.assertThrows(
                ValidationException.class,
                () -> new Cpf("12345678900"));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve instanciar um CPF nulo")
    void testNullCpf() {
        // given
        final var expectedError = "Invalid value for Cpf";

        // when
        final var actualException = Assertions.assertThrows(
                ValidationException.class,
                () -> new Cpf(null));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }
}
