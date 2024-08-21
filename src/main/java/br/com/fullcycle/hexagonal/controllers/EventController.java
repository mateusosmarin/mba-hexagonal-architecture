package br.com.fullcycle.hexagonal.controllers;

import static org.springframework.http.HttpStatus.CREATED;

import java.net.URI;
import java.time.Instant;
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
import br.com.fullcycle.hexagonal.dtos.EventDTO;
import br.com.fullcycle.hexagonal.dtos.SubscribeDTO;
import br.com.fullcycle.hexagonal.models.Ticket;
import br.com.fullcycle.hexagonal.models.TicketStatus;
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

        final var maybeCustomer = customerService.findById(dto.getCustomerId());
        if (maybeCustomer.isEmpty()) {
            return ResponseEntity.unprocessableEntity().body("Customer not found");
        }

        final var maybeEvent = eventService.findById(id);
        if (maybeEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        final var maybeTicket = eventService.findTicketByEventIdAndCustomerId(id, dto.getCustomerId());
        if (maybeTicket.isPresent()) {
            return ResponseEntity.unprocessableEntity().body("Email already registered");
        }

        final var customer = maybeCustomer.get();
        final var event = maybeEvent.get();

        if (event.getTotalSpots() < event.getTickets().size() + 1) {
            throw new RuntimeException("Event sold out");
        }

        final var ticket = new Ticket();
        ticket.setEvent(event);
        ticket.setCustomer(customer);
        ticket.setReservedAt(Instant.now());
        ticket.setStatus(TicketStatus.PENDING);

        event.getTickets().add(ticket);

        eventService.save(event);

        return ResponseEntity.ok(new EventDTO(event));
    }
}
