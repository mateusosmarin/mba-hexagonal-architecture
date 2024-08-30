package br.com.fullcycle.hexagonal.application.usecases.ticket;

import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.event.EventId;
import br.com.fullcycle.hexagonal.application.domain.event.EventTicketId;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.Ticket;
import br.com.fullcycle.hexagonal.application.repositories.TicketRepository;
import br.com.fullcycle.hexagonal.application.usecases.UseCase;

public class CreateTicketForCustomerUseCase
        extends UseCase<CreateTicketForCustomerUseCase.Input, CreateTicketForCustomerUseCase.Output> {
    private final TicketRepository ticketRepository;

    public CreateTicketForCustomerUseCase(final TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public record Input(String eventTicketId, String eventId, String customerId) {
    };

    public record Output(String ticketId) {
    };

    @Override
    public Output execute(final Input input) {
        final var ticket = Ticket.newTicket(EventTicketId.with(input.eventTicketId), CustomerId.with(input.customerId),
                EventId.with(input.eventId));
        ticketRepository.create(ticket);
        return new Output(ticket.id().value());
    }
}
