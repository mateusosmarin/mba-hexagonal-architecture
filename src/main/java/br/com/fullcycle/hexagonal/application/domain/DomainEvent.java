package br.com.fullcycle.hexagonal.application.domain;

import java.time.Instant;

public interface DomainEvent {
    String id();

    String type();

    Instant occuredOn();
}
