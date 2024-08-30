package br.com.fullcycle.hexagonal.application.domain.event;

import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.TicketId;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

public class EventTicket {
    private final EventTicketId id;
    private final EventId eventId;
    private final CustomerId customerId;

    private TicketId ticketId;
    private int ordering;

    public EventTicket(
            final EventTicketId id,
            final EventId eventId,
            final CustomerId customerId,
            final TicketId ticketId,
            final Integer ordering) {
        if (id == null) {
            throw new ValidationException("Invalid id for EventTicket");
        }
        if (eventId == null) {
            throw new ValidationException("Invalid eventId for EventTicket");
        }
        if (customerId == null) {
            throw new ValidationException("Invalid customerId for EventTicket");
        }
        this.id = id;
        this.eventId = eventId;
        this.customerId = customerId;
        this.ticketId = ticketId;
        this.setOrdering(ordering);
    }

    public static EventTicket newTicket(final EventId eventId, final CustomerId customerId, final int ordering) {
        return new EventTicket(EventTicketId.unique(), eventId, customerId, null, ordering);
    }

    public EventTicket associateTicket(final TicketId ticketId) {
        this.ticketId = ticketId;
        return this;
    }

    public EventTicketId id() {
        return id;
    }

    public TicketId ticketId() {
        return ticketId;
    }

    public EventId eventId() {
        return eventId;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public int ordering() {
        return ordering;
    }

    private void setOrdering(final Integer ordering) {
        if (ordering == null) {
            throw new ValidationException("Invalid ordering for EventTicket");
        }
        this.ordering = ordering;
    }
}
