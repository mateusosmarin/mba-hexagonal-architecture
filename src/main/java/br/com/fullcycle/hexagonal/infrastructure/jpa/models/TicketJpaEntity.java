package br.com.fullcycle.hexagonal.infrastructure.jpa.models;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.event.EventId;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.Ticket;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.TicketId;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.TicketStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "Ticket")
@Table(name = "tickets")
public class TicketJpaEntity {

    @Id
    private UUID id;

    private UUID customerId;

    private UUID eventId;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    private Instant paidAt;

    private Instant reservedAt;

    public TicketJpaEntity() {
    }

    public TicketJpaEntity(UUID id, UUID customerId, UUID eventId, TicketStatus status,
            Instant paidAt, Instant reservedAt) {
        this.id = id;
        this.customerId = customerId;
        this.eventId = eventId;
        this.status = status;
        this.paidAt = paidAt;
        this.reservedAt = reservedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID ticketId) {
        this.customerId = ticketId;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEvent(UUID eventId) {
        this.eventId = eventId;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }

    public Instant getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(Instant reservedAt) {
        this.reservedAt = reservedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TicketJpaEntity ticket = (TicketJpaEntity) o;
        return Objects.equals(id, ticket.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, eventId);
    }

    public Ticket toTicket() {
        return new Ticket(
                TicketId.with(id.toString()),
                CustomerId.with(customerId.toString()),
                EventId.with(eventId.toString()),
                status,
                paidAt,
                reservedAt);
    }

    public static TicketJpaEntity of(Ticket ticket) {
        return new TicketJpaEntity(
                UUID.fromString(ticket.id().value()),
                UUID.fromString(ticket.customerId().value()),
                UUID.fromString(ticket.eventId().value()),
                ticket.status(),
                ticket.paidAt(),
                ticket.reservedAt());
    }
}
