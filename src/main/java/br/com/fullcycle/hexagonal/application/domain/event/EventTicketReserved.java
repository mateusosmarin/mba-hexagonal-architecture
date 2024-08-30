package br.com.fullcycle.hexagonal.application.domain.event;

import java.time.Instant;
import java.util.UUID;

import br.com.fullcycle.hexagonal.application.domain.DomainEvent;
import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;

public record EventTicketReserved(
        String id,
        String type,
        Instant occuredOn,
        String eventTicketId,
        String eventId,
        String customerId)
        implements DomainEvent {
    public EventTicketReserved(EventTicketId eventTicketId, EventId eventId, CustomerId customerId) {
        this(UUID.randomUUID().toString(), "event-ticket.reserved", Instant.now(),
                eventTicketId.value(), eventId.value(), customerId.value());
    }
}
