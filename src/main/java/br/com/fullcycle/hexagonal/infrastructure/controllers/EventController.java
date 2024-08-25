package br.com.fullcycle.hexagonal.infrastructure.controllers;

import static org.springframework.http.HttpStatus.CREATED;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.usecases.CreateEventUseCase;
import br.com.fullcycle.hexagonal.application.usecases.SubscribeCustomerToEventUseCase;
import br.com.fullcycle.hexagonal.infrastructure.dtos.CreateEventDTO;
import br.com.fullcycle.hexagonal.infrastructure.dtos.SubscribeDTO;

// Adapter
@RestController
@RequestMapping(value = "events")
public class EventController {
    private final CreateEventUseCase createEventUseCase;
    private final SubscribeCustomerToEventUseCase subscribeCustomerToEventUseCase;

    public EventController(
            final CreateEventUseCase createEventUseCase,
            final SubscribeCustomerToEventUseCase subscribeCustomerToEventUseCase) {
        this.createEventUseCase = createEventUseCase;
        this.subscribeCustomerToEventUseCase = subscribeCustomerToEventUseCase;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<?> create(@RequestBody final CreateEventDTO dto) {
        try {
            final var input = new CreateEventUseCase.Input(dto.date(), dto.name(), dto.partnerId(), dto.totalSpots());
            final var output = createEventUseCase.execute(input);
            return ResponseEntity.created(URI.create("/events/" + output.id())).body(output);
        } catch (final ValidationException ex) {
            return ResponseEntity.unprocessableEntity().body(ex.getMessage());
        }
    }

    @Transactional
    @PostMapping(value = "/{id}/subscribe")
    public ResponseEntity<?> subscribe(@PathVariable final Long id, @RequestBody final SubscribeDTO dto) {
        try {
            final var input = new SubscribeCustomerToEventUseCase.Input(id, dto.customerId());
            final var output = subscribeCustomerToEventUseCase.execute(input);
            return ResponseEntity.ok(output);
        } catch (final ValidationException ex) {
            return ResponseEntity.unprocessableEntity().body(ex.getMessage());
        }
    }
}
