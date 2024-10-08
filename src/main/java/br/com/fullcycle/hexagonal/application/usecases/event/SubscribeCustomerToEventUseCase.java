package br.com.fullcycle.hexagonal.application.usecases.event;

import java.time.Instant;

import br.com.fullcycle.hexagonal.application.usecases.UseCase;
import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.event.EventId;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.repositories.CustomerRepository;
import br.com.fullcycle.hexagonal.application.repositories.EventRepository;

public class SubscribeCustomerToEventUseCase
        extends UseCase<SubscribeCustomerToEventUseCase.Input, SubscribeCustomerToEventUseCase.Output> {
    public record Input(String eventId, String customerId) {
    }

    public record Output(String eventId, String eventTicketId, Instant reservationDate) {
    }

    private final CustomerRepository customerRepository;
    private final EventRepository eventRepository;

    public SubscribeCustomerToEventUseCase(
            final CustomerRepository customerRepository,
            final EventRepository eventRepository) {
        this.customerRepository = customerRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public Output execute(final Input input) {
        final var customer = customerRepository.getById(CustomerId.with(input.customerId))
                .orElseThrow(() -> new ValidationException("Customer not found"));

        final var event = eventRepository.getById(EventId.with(input.eventId))
                .orElseThrow(() -> new ValidationException("Event not found"));

        final var ticket = event.reserveTicket(customer.id());

        eventRepository.update(event);

        return new Output(event.id().value(), ticket.id().value(), Instant.now());
    }
}
