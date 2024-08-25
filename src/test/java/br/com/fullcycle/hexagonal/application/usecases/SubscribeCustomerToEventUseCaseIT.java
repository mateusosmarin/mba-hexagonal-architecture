package br.com.fullcycle.hexagonal.application.usecases;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import br.com.fullcycle.hexagonal.IntegrationTest;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.models.Customer;
import br.com.fullcycle.hexagonal.infrastructure.models.Event;
import br.com.fullcycle.hexagonal.infrastructure.models.Partner;
import br.com.fullcycle.hexagonal.infrastructure.models.TicketStatus;
import br.com.fullcycle.hexagonal.infrastructure.repositories.CustomerRepository;
import br.com.fullcycle.hexagonal.infrastructure.repositories.EventRepository;
import br.com.fullcycle.hexagonal.infrastructure.repositories.PartnerRepository;
import br.com.fullcycle.hexagonal.infrastructure.repositories.TicketRepository;
import io.hypersistence.tsid.TSID;

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
        customer = createCustomer("12345678901", "john.doe@gmail.com", "John Doe");
        partner = createPartner("41536538000100", "disney@gmail.com", "Disney");
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
        final var customerId = customer.getId();
        final var eventId = event.getId();

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

        final var customerId = customer.getId();
        final var eventId = TSID.fast().toLong();

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

        final var customerId = TSID.fast().toLong();
        final var eventId = event.getId();

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

        final var customerId = customer.getId();
        final var eventId = event.getId();

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

        final var customerId = customer.getId();
        final var eventId = event.getId();

        final var input = new SubscribeCustomerToEventUseCase.Input(eventId, customerId);

        event.setTotalSpots(event.getTickets().size());

        // when
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(input));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    private Customer createCustomer(final String cpf, final String email, final String name) {
        final var customer = new Customer();
        customer.setCpf(cpf);
        customer.setEmail(email);
        customer.setName(name);
        return customerRepository.save(customer);
    }

    private Partner createPartner(final String cnpj, final String email, final String name) {
        final var partner = new Partner();
        partner.setCnpj(cnpj);
        partner.setEmail(email);
        partner.setName(name);
        return partnerRepository.save(partner);
    }

    private Event createEvent(final String date, final String name, final Partner partner, final int totalSpots) {
        final var event = new Event();
        event.setDate(LocalDate.parse(date, DateTimeFormatter.ISO_DATE));
        event.setName(name);
        event.setPartner(partner);
        event.setTotalSpots(totalSpots);
        return eventRepository.save(event);
    }
}
