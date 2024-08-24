package br.com.fullcycle.hexagonal.application.usecases;

import java.time.Instant;

import br.com.fullcycle.hexagonal.application.UseCase;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.models.Ticket;
import br.com.fullcycle.hexagonal.infrastructure.models.TicketStatus;
import br.com.fullcycle.hexagonal.infrastructure.services.CustomerService;
import br.com.fullcycle.hexagonal.infrastructure.services.EventService;

public class SubscribeCustomerToEventUseCase
        extends UseCase<SubscribeCustomerToEventUseCase.Input, SubscribeCustomerToEventUseCase.Output> {
    public record Input(Long eventId, Long customerId) {
    }

    public record Output(Long eventId, String ticketStatus, Instant reservationDate) {
    }

    private final CustomerService customerService;
    private final EventService eventService;

    public SubscribeCustomerToEventUseCase(final CustomerService customerService, final EventService eventService) {
        this.customerService = customerService;
        this.eventService = eventService;
    }

    @Override
    public Output execute(final Input input) {
        final var customer = customerService.findById(input.customerId)
                .orElseThrow(() -> new ValidationException("Customer not found"));

        final var event = eventService.findById(input.eventId)
                .orElseThrow(() -> new ValidationException("Event not found"));

        eventService.findTicketByEventIdAndCustomerId(input.eventId(), input.customerId)
                .ifPresent(t -> {
                    throw new ValidationException("Email already registered");
                });

        if (event.getTotalSpots() < event.getTickets().size() + 1) {
            throw new ValidationException("Event sold out");
        }

        final var ticket = new Ticket();
        ticket.setEvent(event);
        ticket.setCustomer(customer);
        ticket.setReservedAt(Instant.now());
        ticket.setStatus(TicketStatus.PENDING);

        event.getTickets().add(ticket);

        eventService.save(event);

        return new Output(event.getId(), ticket.getStatus().name(), ticket.getReservedAt());
    }
}
