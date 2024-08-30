package br.com.fullcycle.hexagonal.application.domain.event;

import java.util.UUID;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

public record EventTicketId(String value) {
    public EventTicketId {
        if (value == null) {
            throw new ValidationException("Invalid value for EventTicketId");
        }
    }

    public static EventTicketId unique() {
        return new EventTicketId(UUID.randomUUID().toString());
    }

    public static EventTicketId with(final String value) {
        try {
            return new EventTicketId(UUID.fromString(value).toString());
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Invalid value for EventTicketId");
        }
    }
}
