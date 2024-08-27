package br.com.fullcycle.hexagonal.application.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

class NameTest {
    @Test
    @DisplayName("Deve instanciar um nome válido")
    void testValidName() {
        // given
        final var expectedName = "John Doe";

        // when
        final var actualName = new Name(expectedName);

        // then
        Assertions.assertEquals(expectedName, actualName.value());
    }

    @Test
    @DisplayName("Não deve instanciar um nome nulo")
    void testNullName() {
        // given
        final var expectedError = "Invalid value for Name";

        // when
        final var actualException = Assertions.assertThrows(
                ValidationException.class,
                () -> new Name(null));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }
}
