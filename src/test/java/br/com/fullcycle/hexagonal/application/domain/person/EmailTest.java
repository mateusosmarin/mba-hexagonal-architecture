package br.com.fullcycle.hexagonal.application.domain.person;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

class EmailTest {
    @Test
    @DisplayName("Deve instanciar um e-mail válido")
    void testValidEmail() {
        // given
        final var expectedEmail = "john.doe@gmail.com";

        // when
        final var actualEmail = new Email(expectedEmail);

        // then
        Assertions.assertEquals(expectedEmail, actualEmail.value());
    }

    @Test
    @DisplayName("Não deve instanciar um e-mail inválido")
    void testInvalidEmail() {
        // given
        final var expectedError = "Invalid value for Email";

        // when
        final var actualException = Assertions.assertThrows(
                ValidationException.class,
                () -> new Email("john.doe@teste"));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve instanciar um e-mail nulo")
    void testNullEmail() {
        // given
        final var expectedError = "Invalid value for Email";

        // when
        final var actualException = Assertions.assertThrows(
                ValidationException.class,
                () -> new Email(null));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }
}
