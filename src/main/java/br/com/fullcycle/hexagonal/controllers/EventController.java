package br.com.fullcycle.hexagonal.controllers;

import static org.springframework.http.HttpStatus.CREATED;

import java.net.URI;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
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
import br.com.fullcycle.hexagonal.dtos.EventDTO;
import br.com.fullcycle.hexagonal.dtos.SubscribeDTO;
import br.com.fullcycle.hexagonal.services.CustomerService;
import br.com.fullcycle.hexagonal.services.EventService;
import br.com.fullcycle.hexagonal.services.PartnerService;

// Adapter
@RestController
@RequestMapping(value = "events")
public class EventController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EventService eventService;

    @Autowired
    private PartnerService partnerService;

    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<?> create(@RequestBody final EventDTO dto) {
        try {
            final var useCase = new CreateEventUseCase(eventService, partnerService);
            final var partnerId = Objects.requireNonNull(dto.getPartner(), "Partner is required").getId();
            final var input = new CreateEventUseCase.Input(
                    dto.getDate(), dto.getName(), partnerId, dto.getTotalSpots());
            final var output = useCase.execute(input);
            return ResponseEntity.created(URI.create("/events/" + output.id())).body(output);
        } catch (final ValidationException ex) {
            return ResponseEntity.unprocessableEntity().body(ex.getMessage());
        }
    }

    @Transactional
    @PostMapping(value = "/{id}/subscribe")
    public ResponseEntity<?> subscribe(@PathVariable final Long id, @RequestBody final SubscribeDTO dto) {
        try {
            final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
            final var input = new SubscribeCustomerToEventUseCase.Input(id, dto.getCustomerId());
            final var output = useCase.execute(input);
            return ResponseEntity.ok(output);
        } catch (final ValidationException ex) {
            return ResponseEntity.unprocessableEntity().body(ex.getMessage());
        }
    }
}
