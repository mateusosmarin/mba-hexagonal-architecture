package br.com.fullcycle.hexagonal.application.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

class CnpjTest {
    @Test
    @DisplayName("Deve instanciar um CNPJ válido")
    void testValidCnpj() {
        // given
        final var expectedCnpj = "12.345.678/0001-00";

        // when
        final var actualCnpj = new Cnpj(expectedCnpj);

        // then
        Assertions.assertEquals(expectedCnpj, actualCnpj.value());
    }

    @Test
    @DisplayName("Não deve instanciar um CNPJ inválido")
    void testInvalidCnpj() {
        // given
        final var expectedError = "Invalid value for Cnpj";

        // when
        final var actualException = Assertions.assertThrows(
                ValidationException.class,
                () -> new Cnpj("12345678000100"));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve instanciar um CNPJ nulo")
    void testNullCnpj() {
        // given
        final var expectedError = "Invalid value for Cnpj";

        // when
        final var actualException = Assertions.assertThrows(
                ValidationException.class,
                () -> new Cnpj(null));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }
}
