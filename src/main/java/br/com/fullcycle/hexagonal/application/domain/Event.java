package br.com.fullcycle.hexagonal.application.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

public class Event {
    private final EventId id;
    private Name name;
    private LocalDate date;
    private int totalSpots;
    private PartnerId partnerId;
    private Set<EventTicket> tickets;

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
    }

    public static Event newEvent(
            final String name,
            final String date,
            final Integer totalSpots,
            final Partner partner) {
        return new Event(EventId.unique(), name, date, totalSpots, partner.id(), null);
    }

    public Ticket reserveTicket(CustomerId customerId) {
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

        final var ticket = Ticket.newTicket(customerId, id());
        tickets.add(new EventTicket(ticket.id(), id(), customerId, allTickets().size() + 1));

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
