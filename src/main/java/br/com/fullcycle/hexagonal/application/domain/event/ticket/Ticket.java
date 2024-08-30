package br.com.fullcycle.hexagonal.application.domain.event.ticket;

import java.time.Instant;
import java.util.Objects;

import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.event.EventId;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

public class Ticket {
    private final TicketId id;
    private CustomerId customerId;
    private EventId eventId;
    private TicketStatus status;
    private Instant paidAt;
    private Instant reservedAt;

    public Ticket(final TicketId id,
            final CustomerId customerId,
            final EventId eventId,
            final TicketStatus status,
            final Instant paidAt,
            final Instant reservedAt) {
        this.id = id;
        this.setCustomerId(customerId);
        this.setEventId(eventId);
        this.setStatus(status);
        this.setPaidAt(paidAt);
        this.setReservedAt(reservedAt);

    }

    public static Ticket newTicket(final CustomerId customerId, final EventId eventId) {
        return new Ticket(TicketId.unique(), customerId, eventId, TicketStatus.PENDING, null, Instant.now());
    }

    public TicketId id() {
        return id;
    }

    public CustomerId customerId() {
        return customerId;
    }

    private void setCustomerId(final CustomerId customerId) {
        if (customerId == null) {
            throw new ValidationException("Invalid customerId for Ticket");
        }
        this.customerId = customerId;
    }

    public EventId eventId() {
        return eventId;
    }

    private void setEventId(final EventId eventId) {
        if (eventId == null) {
            throw new ValidationException("Invalid eventId for Ticket");
        }
        this.eventId = eventId;
    }

    public TicketStatus status() {
        return status;
    }

    private void setStatus(final TicketStatus status) {
        if (status == null) {
            throw new ValidationException("Invalid status for Ticket");
        }
        this.status = status;
    }

    public Instant paidAt() {
        return paidAt;
    }

    private void setPaidAt(final Instant paidAt) {
        this.paidAt = paidAt;
    }

    public Instant reservedAt() {
        return reservedAt;
    }

    private void setReservedAt(final Instant reservedAt) {
        if (reservedAt == null) {
            throw new ValidationException("Invalid reservedAt for Ticket");
        }
        this.reservedAt = reservedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(id, ticket.id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
