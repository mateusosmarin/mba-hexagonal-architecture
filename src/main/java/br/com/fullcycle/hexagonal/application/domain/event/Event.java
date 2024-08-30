package br.com.fullcycle.hexagonal.application.domain.event;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import br.com.fullcycle.hexagonal.application.domain.DomainEvent;
import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.partner.Partner;
import br.com.fullcycle.hexagonal.application.domain.partner.PartnerId;
import br.com.fullcycle.hexagonal.application.domain.person.Name;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

public class Event {
    private final EventId id;
    private final Set<EventTicket> tickets;
    private final Set<DomainEvent> domainEvents;

    private Name name;
    private LocalDate date;
    private int totalSpots;
    private PartnerId partnerId;

    public Event(
            final EventId id,
            final String name,
            final String date,
            final Integer totalSpots,
            final PartnerId partnerId,
            final Set<EventTicket> tickets) {
        this(id, tickets);
        setName(name);
        setDate(date);
        setTotalSpots(totalSpots);
        setPartnerId(partnerId);
    }

    private Event(final EventId id, final Set<EventTicket> tickets) {
        if (id == null) {
            throw new ValidationException("Invalid id for Event");
        }
        this.id = id;
        this.tickets = tickets != null ? tickets : new HashSet<>(0);
        this.domainEvents = new HashSet<>(2);
    }

    public static Event newEvent(
            final String name,
            final String date,
            final Integer totalSpots,
            final Partner partner) {
        return new Event(EventId.unique(), name, date, totalSpots, partner.id(), null);
    }

    public EventTicket reserveTicket(CustomerId customerId) {
        allTickets()
                .stream()
                .filter(it -> Objects.equals(it.customerId(), customerId))
                .findFirst()
                .ifPresent(it -> {
                    throw new ValidationException("Email already registered");
                });

        if (allTickets().size() + 1 > totalSpots()) {
            throw new ValidationException("Event sold out");
        }

        final var ticket = EventTicket.newTicket(id(), customerId, allTickets().size() + 1);
        tickets.add(ticket);
        domainEvents.add(new EventTicketReserved(ticket.id(), id(), customerId));

        return ticket;
    }

    public EventId id() {
        return id;
    }

    public Name name() {
        return name;
    }

    private void setName(final String name) {
        this.name = new Name(name);
    }

    public LocalDate date() {
        return date;
    }

    private void setDate(final String date) {
        if (date == null) {
            throw new ValidationException("Invalid date for Event");
        }
        try {
            this.date = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (RuntimeException ex) {
            throw new ValidationException("Invalid date for Event", ex);
        }
    }

    public int totalSpots() {
        return totalSpots;
    }

    private void setTotalSpots(final Integer totalSpots) {
        if (totalSpots == null) {
            throw new ValidationException("Invalid totalSpots for Event");
        }
        this.totalSpots = totalSpots;
    }

    public PartnerId partnerId() {
        return partnerId;
    }

    private void setPartnerId(final PartnerId partnerId) {
        if (partnerId == null) {
            throw new ValidationException("Invalid partnerId for Event");
        }
        this.partnerId = partnerId;
    }

    public Set<EventTicket> allTickets() {
        return Collections.unmodifiableSet(tickets);
    }

    public Set<DomainEvent> allDomainEvents() {
        return Collections.unmodifiableSet(domainEvents);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static Event restore(
            final String id,
            final String name,
            final String date,
            final int totalSpots,
            final String partnerId,
            final Set<EventTicket> tickets) {
        return new Event(
                EventId.with(id),
                name,
                date,
                totalSpots,
                PartnerId.with(partnerId),
                tickets);
    }
}
