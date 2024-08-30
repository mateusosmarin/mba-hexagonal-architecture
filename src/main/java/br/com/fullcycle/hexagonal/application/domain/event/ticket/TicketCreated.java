package br.com.fullcycle.hexagonal.application.domain.event.ticket;

import java.time.Instant;
import java.util.UUID;

import br.com.fullcycle.hexagonal.application.domain.DomainEvent;
import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.event.EventId;
import br.com.fullcycle.hexagonal.application.domain.event.EventTicketId;

public record TicketCreated(
        String id,
        String type,
        Instant occuredOn,
        String ticketId,
        String eventTicketId,
        String eventId,
        String customerId)
        implements DomainEvent {
    public TicketCreated(TicketId ticketId, EventTicketId eventTicketId, EventId eventId, CustomerId customerId) {
        this(UUID.randomUUID().toString(), "ticket.created", Instant.now(),
                ticketId.value(), eventTicketId.value(), eventId.value(), customerId.value());
    }
}
