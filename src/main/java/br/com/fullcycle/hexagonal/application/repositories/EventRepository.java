package br.com.fullcycle.hexagonal.application.repositories;

import java.util.Optional;

import br.com.fullcycle.hexagonal.application.domain.Event;
import br.com.fullcycle.hexagonal.application.domain.EventId;

public interface EventRepository {
    Optional<Event> getById(EventId id);

    Event create(Event partner);

    Event update(Event partner);

    void deleteAll();
}
