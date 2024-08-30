package br.com.fullcycle.hexagonal.infrastructure.repositories;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fullcycle.hexagonal.application.domain.DomainEvent;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.Ticket;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.TicketId;
import br.com.fullcycle.hexagonal.application.repositories.TicketRepository;
import br.com.fullcycle.hexagonal.infrastructure.jpa.models.OutboxJpaEntity;
import br.com.fullcycle.hexagonal.infrastructure.jpa.models.TicketJpaEntity;
import br.com.fullcycle.hexagonal.infrastructure.jpa.repositories.OutboxJpaRepository;
import br.com.fullcycle.hexagonal.infrastructure.jpa.repositories.TicketJpaRepository;

@Component
public class TicketDatabaseRepository implements TicketRepository {
    private final TicketJpaRepository ticketJpaRepository;
    private final OutboxJpaRepository outboxJpaRepository;
    private final ObjectMapper mapper;

    public TicketDatabaseRepository(final TicketJpaRepository ticketJpaRepository,
            final OutboxJpaRepository outboxJpaRepository, final ObjectMapper mapper) {
        this.ticketJpaRepository = ticketJpaRepository;
        this.outboxJpaRepository = outboxJpaRepository;
        this.mapper = mapper;
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
        return save(ticket);
    }

    @Override
    @Transactional
    public Ticket update(Ticket ticket) {
        return save(ticket);
    }

    public void deleteAll() {
        ticketJpaRepository.deleteAll();
    }

    private Ticket save(Ticket ticket) {
        outboxJpaRepository.saveAll(ticket.allDomainEvents().stream()
                .map(it -> OutboxJpaEntity.of(it, this::toJson))
                .toList());
        return ticketJpaRepository.save(TicketJpaEntity.of(ticket)).toTicket();
    }

    private String toJson(DomainEvent domainEvent) {
        try {
            return mapper.writeValueAsString(domainEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
