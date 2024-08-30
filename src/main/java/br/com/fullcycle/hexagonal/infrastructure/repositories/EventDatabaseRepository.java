package br.com.fullcycle.hexagonal.infrastructure.repositories;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fullcycle.hexagonal.application.domain.DomainEvent;
import br.com.fullcycle.hexagonal.application.domain.event.Event;
import br.com.fullcycle.hexagonal.application.domain.event.EventId;
import br.com.fullcycle.hexagonal.application.repositories.EventRepository;
import br.com.fullcycle.hexagonal.infrastructure.jpa.models.EventJpaEntity;
import br.com.fullcycle.hexagonal.infrastructure.jpa.models.OutboxJpaEntity;
import br.com.fullcycle.hexagonal.infrastructure.jpa.repositories.EventJpaRepository;
import br.com.fullcycle.hexagonal.infrastructure.jpa.repositories.OutboxJpaRepository;

@Component
public class EventDatabaseRepository implements EventRepository {
    private final EventJpaRepository eventJpaRepository;
    private final OutboxJpaRepository outboxJpaRepository;
    private final ObjectMapper mapper;

    public EventDatabaseRepository(final EventJpaRepository eventJpaRepository,
            final OutboxJpaRepository outboxJpaRepository,
            final ObjectMapper mapper) {
        this.eventJpaRepository = eventJpaRepository;
        this.outboxJpaRepository = outboxJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Event> getById(EventId id) {
        Objects.requireNonNull(id, "id cannot be null");
        return eventJpaRepository
                .findById(UUID.fromString(id.value()))
                .map(it -> it.toEvent());
    }

    @Override
    @Transactional
    public Event create(Event event) {
        return save(event);
    }

    @Override
    @Transactional
    public Event update(Event event) {
        return save(event);
    }

    private Event save(Event event) {
        outboxJpaRepository.saveAll(event.allDomainEvents().stream()
                .map(it -> OutboxJpaEntity.of(it, this::toJson))
                .toList());
        return eventJpaRepository.save(EventJpaEntity.of(event)).toEvent();
    }

    public void deleteAll() {
        eventJpaRepository.deleteAll();
    }

    private String toJson(DomainEvent domainEvent) {
        try {
            return mapper.writeValueAsString(domainEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
