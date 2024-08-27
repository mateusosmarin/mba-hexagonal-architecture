package br.com.fullcycle.hexagonal.application.usecases;

import java.time.format.DateTimeFormatter;

import br.com.fullcycle.hexagonal.application.UseCase;
import br.com.fullcycle.hexagonal.application.domain.Event;
import br.com.fullcycle.hexagonal.application.domain.PartnerId;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.repositories.EventRepository;
import br.com.fullcycle.hexagonal.application.repositories.PartnerRepository;

public class CreateEventUseCase extends UseCase<CreateEventUseCase.Input, CreateEventUseCase.Output> {
    public record Input(String date, String name, String partnerId, Integer totalSpots) {
    }

    public record Output(String id, String date, String name, String partnerId, Integer totalSpots) {
    }

    private final EventRepository eventRepository;
    private final PartnerRepository partnerRepository;

    public CreateEventUseCase(final EventRepository eventRepository, final PartnerRepository partnerRepository) {
        this.eventRepository = eventRepository;
        this.partnerRepository = partnerRepository;
    }

    @Override
    public Output execute(Input input) {
        final var partner = partnerRepository.getById(PartnerId.with(input.partnerId))
                .orElseThrow(() -> new ValidationException("Partner not found"));

        final var event = eventRepository.create(Event.newEvent(input.name, input.date, input.totalSpots, partner));

        return new Output(
                event.id().value(),
                event.date().format(DateTimeFormatter.ISO_DATE),
                event.name().value(),
                event.partnerId().value(),
                event.totalSpots());
    }
}
