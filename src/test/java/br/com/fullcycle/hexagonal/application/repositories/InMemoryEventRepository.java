package br.com.fullcycle.hexagonal.application.repositories;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import br.com.fullcycle.hexagonal.application.domain.Event;
import br.com.fullcycle.hexagonal.application.domain.EventId;

public class InMemoryEventRepository implements EventRepository {
    private final Map<String, Event> events;

    public InMemoryEventRepository() {
        this.events = new HashMap<>();
    }

    public Optional<Event> getById(EventId id) {
        return Optional.ofNullable(this.events.get(Objects.requireNonNull(id).value()));
    }

    public Event create(Event event) {
        this.events.put(event.id().value(), event);
        return event;
    }

    public Event update(Event event) {
        this.events.put(event.id().value(), event);
        return event;
    }

    public void deleteAll() {
        this.events.clear();
    }
}
