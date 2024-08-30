package br.com.fullcycle.hexagonal.infrastructure.repositories;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.fullcycle.hexagonal.application.domain.event.ticket.Ticket;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.TicketId;
import br.com.fullcycle.hexagonal.application.repositories.TicketRepository;
import br.com.fullcycle.hexagonal.infrastructure.jpa.models.TicketJpaEntity;
import br.com.fullcycle.hexagonal.infrastructure.jpa.repositories.TicketJpaRepository;

@Component
public class TicketDatabaseRepository implements TicketRepository {
    private final TicketJpaRepository ticketJpaRepository;

    public TicketDatabaseRepository(final TicketJpaRepository ticketJpaRepository) {
        this.ticketJpaRepository = ticketJpaRepository;
    }

    @Override
    public Optional<Ticket> getById(TicketId id) {
        Objects.requireNonNull(id, "id cannot be null");
        return this.ticketJpaRepository
                .findById(UUID.fromString(id.value()))
                .map(it -> it.toTicket());
    }

    @Override
    @Transactional
    public Ticket create(Ticket ticket) {
        return this.ticketJpaRepository.save(TicketJpaEntity.of(ticket)).toTicket();
    }

    @Override
    @Transactional
    public Ticket update(Ticket ticket) {
        return this.ticketJpaRepository.save(TicketJpaEntity.of(ticket)).toTicket();
    }

    public void deleteAll() {
        this.ticketJpaRepository.deleteAll();
    }
}
