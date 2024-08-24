package br.com.fullcycle.hexagonal.application.usecases;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import br.com.fullcycle.hexagonal.application.UseCase;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.models.Event;
import br.com.fullcycle.hexagonal.infrastructure.services.EventService;
import br.com.fullcycle.hexagonal.infrastructure.services.PartnerService;

public class CreateEventUseCase extends UseCase<CreateEventUseCase.Input, CreateEventUseCase.Output> {
    public record Input(String date, String name, Long partnerId, Integer totalSpots) {
    }

    public record Output(Long id, String date, String name, Long partnerId, Integer totalSpots) {
    }

    private final EventService eventService;
    private final PartnerService partnerService;

    public CreateEventUseCase(final EventService eventService, final PartnerService partnerService) {
        this.eventService = eventService;
        this.partnerService = partnerService;
    }

    @Override
    public Output execute(Input input) {
        var event = new Event();
        event.setDate(LocalDate.parse(input.date, DateTimeFormatter.ISO_DATE));
        event.setName(input.name);
        event.setTotalSpots(input.totalSpots);

        partnerService.findById(input.partnerId)
                .ifPresentOrElse(event::setPartner, () -> {
                    throw new ValidationException("Partner not found");
                });

        event = eventService.save(event);

        return new Output(event.getId(), event.getDate().format(DateTimeFormatter.ISO_DATE), event.getName(),
                event.getPartner().getId(), event.getTotalSpots());
    }
}
