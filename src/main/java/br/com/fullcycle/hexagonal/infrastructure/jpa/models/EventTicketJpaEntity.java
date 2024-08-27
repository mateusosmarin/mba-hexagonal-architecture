package br.com.fullcycle.hexagonal.infrastructure.jpa.models;

import java.util.Objects;
import java.util.UUID;

import br.com.fullcycle.hexagonal.application.domain.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.EventId;
import br.com.fullcycle.hexagonal.application.domain.EventTicket;
import br.com.fullcycle.hexagonal.application.domain.TicketId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity(name = "EventTicket")
@Table(name = "events_tickets")
public class EventTicketJpaEntity {

    @Id
    private UUID ticketId;

    private UUID customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    private EventJpaEntity event;

    private int ordering;

    public EventTicketJpaEntity() {
    }

    public EventTicketJpaEntity(
            final UUID ticketId,
            final UUID customerId,
            final int ordering,
            final EventJpaEntity event) {
        this.ticketId = ticketId;
        this.customerId = customerId;
        this.ordering = ordering;
        this.event = event;
    }

    public static EventTicketJpaEntity of(final EventJpaEntity event, final EventTicket eventTicket) {
        return new EventTicketJpaEntity(
                UUID.fromString(eventTicket.ticketId().value()),
                UUID.fromString(eventTicket.customerId().value()),
                eventTicket.ordering(),
                event);
    }

    public EventTicket toEventTicket() {
        return new EventTicket(
                TicketId.with(ticketId.toString()),
                EventId.with(event.getId().toString()),
                CustomerId.with(customerId.toString()),
                ordering);
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
        return ordering == that.ordering
                && Objects.equals(ticketId, that.ticketId)
                && Objects.equals(customerId, that.customerId)
                && Objects.equals(event.getId(), that.event.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketId, customerId, ordering, event.getId());
    }
}
