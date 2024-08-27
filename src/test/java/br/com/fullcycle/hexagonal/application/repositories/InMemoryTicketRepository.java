package br.com.fullcycle.hexagonal.application.repositories;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import br.com.fullcycle.hexagonal.application.domain.Ticket;
import br.com.fullcycle.hexagonal.application.domain.TicketId;

public class InMemoryTicketRepository implements TicketRepository {
    private final Map<String, Ticket> tickets;

    public InMemoryTicketRepository() {
        this.tickets = new HashMap<>();
    }

    public Optional<Ticket> getById(TicketId id) {
        return Optional.ofNullable(this.tickets.get(Objects.requireNonNull(id).value()));
    }

    public Ticket create(Ticket ticket) {
        this.tickets.put(ticket.id().value(), ticket);
        return ticket;
    }

    public Ticket update(Ticket ticket) {
        this.tickets.put(ticket.id().value(), ticket);
        return ticket;
    }

    public void deleteAll() {
        this.tickets.clear();
    }
}
