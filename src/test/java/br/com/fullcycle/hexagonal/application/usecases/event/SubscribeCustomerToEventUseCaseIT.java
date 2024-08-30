package br.com.fullcycle.hexagonal.application.usecases.event;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import br.com.fullcycle.hexagonal.IntegrationTest;
import br.com.fullcycle.hexagonal.application.domain.customer.Customer;
import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.event.Event;
import br.com.fullcycle.hexagonal.application.domain.event.EventId;
import br.com.fullcycle.hexagonal.application.domain.partner.Partner;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.TicketStatus;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.repositories.CustomerRepository;
import br.com.fullcycle.hexagonal.application.repositories.EventRepository;
import br.com.fullcycle.hexagonal.application.repositories.PartnerRepository;
import br.com.fullcycle.hexagonal.application.repositories.TicketRepository;

class SubscribeCustomerToEventUseCaseIT extends IntegrationTest {
    @Autowired
    private SubscribeCustomerToEventUseCase useCase;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private Customer customer;
    private Partner partner;
    private Event event;

    @BeforeEach
    void setUp() {
        customer = createCustomer("123.456.789-01", "john.doe@gmail.com", "John Doe");
        partner = createPartner("12.345.678/0001-00", "disney@gmail.com", "Disney");
        event = createEvent("2024-08-24", "Frozen", partner, 10);
    }

    @AfterEach
    void tearDown() {
        ticketRepository.deleteAll();
        eventRepository.deleteAll();
        partnerRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    @Transactional
    @DisplayName("Deve comprar um ticket de um evento")
    public void testReserveTicket() throws Exception {
        // given
        final var customerId = customer.id().value();
        final var eventId = event.id().value();

        final var input = new SubscribeCustomerToEventUseCase.Input(eventId, customerId);

        // when
        final var output = useCase.execute(input);

        // then
        Assertions.assertEquals(eventId, output.eventId());
        Assertions.assertNotNull(output.reservationDate());
        Assertions.assertEquals(TicketStatus.PENDING.name(), output.ticketStatus());
    }

    @Test
    @DisplayName("Não deve comprar um ticket de um evento que não existe")
    public void testReserveTicketWithoutEvent() throws Exception {
        // given
        final var expectedError = "Event not found";

        final var customerId = customer.id().value();
        final var eventId = EventId.unique().value();

        final var input = new SubscribeCustomerToEventUseCase.Input(eventId, customerId);

        // when
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(input));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve comprar um ticket com um cliente não existente")
    public void testReserveTicketWithoutCustomer() throws Exception {
        // given
        final var expectedError = "Customer not found";

        final var customerId = CustomerId.unique().value();
        final var eventId = event.id().value();

        final var input = new SubscribeCustomerToEventUseCase.Input(eventId, customerId);

        // when
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(input));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("Um mesmo cliente não pode comprar mais de um ticket por evento")
    public void testReserveTicketMoreThanOnce() throws Exception {
        // given
        final var expectedError = "Email already registered";

        final var customerId = customer.id().value();
        final var eventId = event.id().value();

        final var input = new SubscribeCustomerToEventUseCase.Input(eventId, customerId);

        // when
        useCase.execute(input);
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(input));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("Um cliente não pode comprar de um evento que não há mais lugares")
    public void testReserveTicketWithoutSpots() throws Exception {
        // given
        final var expectedError = "Event sold out";

        event = createEvent("2024-08-24", "Frozen", partner, 0);

        final var eventId = event.id().value();
        final var customerId = customer.id().value();

        final var input = new SubscribeCustomerToEventUseCase.Input(eventId, customerId);

        // when
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(input));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    private Customer createCustomer(final String cpf, final String email, final String name) {
        return customerRepository.create(Customer.newCustomer(name, cpf, email));
    }

    private Partner createPartner(final String cnpj, final String email, final String name) {
        return partnerRepository.create(Partner.newPartner(name, cnpj, email));
    }

    private Event createEvent(final String date, final String name, final Partner partner, final Integer totalSpots) {
        return eventRepository.create(Event.newEvent(name, date, totalSpots, partner));
    }
}
