package br.com.fullcycle.hexagonal.application.usecases;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.fullcycle.hexagonal.IntegrationTest;
import br.com.fullcycle.hexagonal.infrastructure.models.Partner;
import br.com.fullcycle.hexagonal.infrastructure.repositories.PartnerRepository;

class GetPartnerByIdUseCaseIT extends IntegrationTest {
    @Autowired
    private GetPartnerByIdUseCase useCase;

    @Autowired
    private PartnerRepository partnerRepository;

    @AfterEach
    void tearDown() {
        partnerRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve obter um parceiro por id")
    public void testGetById() {
        // given
        final var expectedCNPJ = "41536538000100";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        final var partner = createPartner(expectedCNPJ, expectedEmail, expectedName);
        final var expectedId = partner.getId();

        final var input = new GetPartnerByIdUseCase.Input(expectedId);

        // when
        final var output = useCase.execute(input).get();

        // then
        Assertions.assertEquals(expectedId, output.id());
        Assertions.assertEquals(expectedCNPJ, output.cnpj());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedName, output.name());
    }

    @Test
    @DisplayName("Deve obter vazio ao tentar recuperar um parceiro não existente por id")
    public void testGetByIdWithInvalidId() {
        // given
        final var expectedId = UUID.randomUUID().getMostSignificantBits();

        final var input = new GetPartnerByIdUseCase.Input(expectedId);

        // when
        final var output = useCase.execute(input);

        // then
        Assertions.assertTrue(output.isEmpty());
    }

    private Partner createPartner(final String cnpj, final String email, final String name) {
        final var partner = new Partner();
        partner.setCnpj(cnpj);
        partner.setEmail(email);
        partner.setName(name);
        return partnerRepository.save(partner);
    }
}
