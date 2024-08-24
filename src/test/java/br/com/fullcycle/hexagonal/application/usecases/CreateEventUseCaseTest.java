package br.com.fullcycle.hexagonal.application.usecases;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.models.Event;
import br.com.fullcycle.hexagonal.infrastructure.models.Partner;
import br.com.fullcycle.hexagonal.infrastructure.services.EventService;
import br.com.fullcycle.hexagonal.infrastructure.services.PartnerService;
import io.hypersistence.tsid.TSID;

class CreateEventUseCaseTest {
    @Test
    @DisplayName("Deve criar um evento")
    public void testCreate() throws RuntimeException {
        // given
        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney on Ice";
        final var expectedTotalSpots = 10;
        final var expectedPartnerId = TSID.fast().toLong();

        final var input = new CreateEventUseCase.Input(expectedDate, expectedName, expectedPartnerId,
                expectedTotalSpots);

        // when
        final var eventService = Mockito.mock(EventService.class);
        final var partnerService = Mockito.mock(PartnerService.class);

        when(eventService.save(any())).thenAnswer(a -> {
            final var e = a.getArgument(0, Event.class);
            e.setId(TSID.fast().toLong());
            return e;
        });

        when(partnerService.findById(eq(expectedPartnerId))).thenAnswer(a -> {
            final var p = new Partner();
            p.setId(expectedPartnerId);
            return Optional.of(p);
        });

        final var useCase = new CreateEventUseCase(eventService, partnerService);
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
        final var expectedPartnerId = TSID.fast().toLong();
        final var expectedError = "Partner not found";

        final var input = new CreateEventUseCase.Input(expectedDate, expectedName, expectedPartnerId,
                expectedTotalSpots);

        // when
        final var eventService = Mockito.mock(EventService.class);
        final var partnerService = Mockito.mock(PartnerService.class);

        when(partnerService.findById(eq(expectedPartnerId))).thenReturn(Optional.empty());

        final var useCase = new CreateEventUseCase(eventService, partnerService);
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(input));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }
}
