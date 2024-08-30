package br.com.fullcycle.hexagonal.application.domain.partner;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

class PartnerTest {
    @Test
    @DisplayName("Deve instanciar um parceiro")
    public void testCreate() {
        // given
        final var expectedCNPJ = "12.345.678/0001-00";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        // when
        final var partner = Partner.newPartner(expectedName, expectedCNPJ, expectedEmail);

        // then
        Assertions.assertNotNull(partner.id());
        Assertions.assertEquals(expectedCNPJ, partner.cnpj().value());
        Assertions.assertEquals(expectedEmail, partner.email().value());
        Assertions.assertEquals(expectedName, partner.name().value());
    }

    @Test
    @DisplayName("Não deve instanciar um parceiro com CNPJ inválido")
    public void testCreateInvalidCnpj() {
        // given
        final var expectedError = "Invalid value for Cnpj";

        // when
        final var actualException = Assertions.assertThrows(ValidationException.class,
                () -> Partner.newPartner("John Doe", "12345678000100", "john.doe@gmail.com"));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve instanciar um parceiro com e-mail inválido")
    public void testCreateInvalidEmail() {
        // given
        final var expectedError = "Invalid value for Email";

        // when
        final var actualException = Assertions.assertThrows(ValidationException.class,
                () -> Partner.newPartner("John Doe", "12.345.678/0001-00", "john.doe@gmail"));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve instanciar um parceiro com nome inválido")
    public void testCreateInvalidName() {
        // given
        final var expectedError = "Invalid value for Name";

        // when
        final var actualException = Assertions.assertThrows(ValidationException.class,
                () -> Partner.newPartner(null, "123.456.789-01", "john.doe@gmail.com"));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }
}
