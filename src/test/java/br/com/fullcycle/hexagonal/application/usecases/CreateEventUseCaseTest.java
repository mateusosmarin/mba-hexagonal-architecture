package br.com.fullcycle.hexagonal.application.usecases;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.fullcycle.hexagonal.application.domain.Partner;
import br.com.fullcycle.hexagonal.application.domain.PartnerId;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.repositories.EventRepository;
import br.com.fullcycle.hexagonal.application.repositories.InMemoryEventRepository;
import br.com.fullcycle.hexagonal.application.repositories.InMemoryPartnerRepository;
import br.com.fullcycle.hexagonal.application.repositories.PartnerRepository;

class CreateEventUseCaseTest {
    private CreateEventUseCase useCase;
    private EventRepository eventRepository;
    private PartnerRepository partnerRepository;

    CreateEventUseCaseTest() {
        eventRepository = new InMemoryEventRepository();
        partnerRepository = new InMemoryPartnerRepository();
        useCase = new CreateEventUseCase(eventRepository, partnerRepository);
    }

    @AfterEach
    void tearDown() {
        eventRepository.deleteAll();
        partnerRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar um evento")
    public void testCreate() throws RuntimeException {
        // given
        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney on Ice";
        final var expectedTotalSpots = 10;

        final var partner = Partner.newPartner("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");
        final var expectedPartnerId = partner.id().value();

        partnerRepository.create(partner);

        final var input = new CreateEventUseCase.Input(
                expectedDate,
                expectedName,
                expectedPartnerId,
                expectedTotalSpots);

        // when
        final var output = useCase.execute(input);

        // then
        Assertions.assertNotNull(output.id());
        Assertions.assertEquals(expectedDate, output.date());
        Assertions.assertEquals(expectedName, output.name());
        Assertions.assertEquals(expectedTotalSpots, output.totalSpots());
        Assertions.assertEquals(expectedPartnerId, output.partnerId());
    }

    @Test
    @DisplayName("Não deve criar um evento quando o Partner não for encontrado")
    public void testCreate_whenPartnerDoesntExists_ShouldThrow() throws RuntimeException {
        // given
        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney on Ice";
        final var expectedTotalSpots = 10;
        final var expectedPartnerId = PartnerId.unique().value();
        final var expectedError = "Partner not found";

        final var input = new CreateEventUseCase.Input(
                expectedDate,
                expectedName,
                expectedPartnerId,
                expectedTotalSpots);

        // when
        final var actualException = Assertions.assertThrows(
                ValidationException.class,
                () -> useCase.execute(input));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }
}
