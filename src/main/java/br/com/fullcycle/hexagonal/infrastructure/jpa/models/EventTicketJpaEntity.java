package br.com.fullcycle.hexagonal.infrastructure.jpa.models;

import java.util.Objects;
import java.util.UUID;

import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.event.EventId;
import br.com.fullcycle.hexagonal.application.domain.event.EventTicket;
import br.com.fullcycle.hexagonal.application.domain.event.EventTicketId;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.TicketId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity(name = "EventTicket")
@Table(name = "events_tickets")
public class EventTicketJpaEntity {

    @Id
    private UUID id;

    private UUID ticketId;

    private UUID customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    private EventJpaEntity event;

    private int ordering;

    public EventTicketJpaEntity() {
    }

    public EventTicketJpaEntity(
            final UUID id,
            final UUID customerId,
            final UUID ticketId,
            final int ordering,
            final EventJpaEntity event) {
        this.id = id;
        this.customerId = customerId;
        this.ticketId = ticketId;
        this.ordering = ordering;
        this.event = event;
    }

    public static EventTicketJpaEntity of(final EventJpaEntity event, final EventTicket eventTicket) {
        return new EventTicketJpaEntity(
                UUID.fromString(eventTicket.id().value()),
                UUID.fromString(eventTicket.customerId().value()),
                eventTicket.ticketId() != null ? UUID.fromString(eventTicket.ticketId().value()) : null,
                eventTicket.ordering(),
                event);
    }

    public EventTicket toEventTicket() {
        return new EventTicket(
                EventTicketId.with(id.toString()),
                EventId.with(event.getId().toString()),
                CustomerId.with(customerId.toString()),
                ticketId != null ? TicketId.with(ticketId.toString()) : null,
                ordering);
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public void setTicketId(final UUID ticketId) {
        this.ticketId = ticketId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(final UUID customerId) {
        this.customerId = customerId;
    }

    public EventJpaEntity getEvent() {
        return event;
    }

    public void setEvent(final EventJpaEntity event) {
        this.event = event;
    }

    public int getOrdering() {
        return ordering;
    }

    public void setOrdering(final int ordering) {
        this.ordering = ordering;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EventTicketJpaEntity that = (EventTicketJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
