package br.com.fullcycle.hexagonal.application.usecases.partner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.fullcycle.hexagonal.application.domain.partner.Partner;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.repositories.InMemoryPartnerRepository;
import br.com.fullcycle.hexagonal.application.repositories.PartnerRepository;

class CreatePartnerUseCaseTest {
    private CreatePartnerUseCase useCase;
    private PartnerRepository partnerRepository;

    CreatePartnerUseCaseTest() {
        partnerRepository = new InMemoryPartnerRepository();
        useCase = new CreatePartnerUseCase(partnerRepository);
    }

    @AfterEach
    void tearDown() {
        partnerRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar um parceiro")
    public void testCreate() {
        // given
        final var expectedCNPJ = "12.345.678/0001-00";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        final var input = new CreatePartnerUseCase.Input(expectedCNPJ, expectedEmail, expectedName);

        // when
        final var output = useCase.execute(input);

        // then
        Assertions.assertNotNull(output.id());
        Assertions.assertEquals(expectedCNPJ, output.cnpj());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedName, output.name());
    }

    @Test
    @DisplayName("Não deve cadastrar um parceiro com CNPJ duplicado")
    public void testCreateWithDuplicatedCNPJShouldFail() {
        // given
        final var expectedCNPJ = "12.345.678/0001-00";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        final var expectedError = "Partner already exists";

        partnerRepository.create(Partner.newPartner(expectedName, expectedCNPJ, "john.doe@hotmail.com"));

        final var input = new CreatePartnerUseCase.Input(expectedCNPJ, expectedEmail, expectedName);

        // when
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(input));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve cadastrar um parceiro com e-mail duplicado")
    public void testCreateWithDuplicatedEmailShouldFail() {
        // given
        final var expectedCNPJ = "12.345.678/0001-00";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        final var expectedError = "Partner already exists";

        partnerRepository.create(Partner.newPartner(expectedName, "12.345.678/0001-01", expectedEmail));

        final var input = new CreatePartnerUseCase.Input(expectedCNPJ, expectedEmail, expectedName);

        // when
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(input));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }
}
