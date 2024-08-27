package br.com.fullcycle.hexagonal.application.usecases;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.fullcycle.hexagonal.application.domain.Customer;
import br.com.fullcycle.hexagonal.application.domain.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.Event;
import br.com.fullcycle.hexagonal.application.domain.EventId;
import br.com.fullcycle.hexagonal.application.domain.Partner;
import br.com.fullcycle.hexagonal.application.domain.TicketStatus;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.repositories.CustomerRepository;
import br.com.fullcycle.hexagonal.application.repositories.EventRepository;
import br.com.fullcycle.hexagonal.application.repositories.InMemoryCustomerRepository;
import br.com.fullcycle.hexagonal.application.repositories.InMemoryEventRepository;
import br.com.fullcycle.hexagonal.application.repositories.InMemoryTicketRepository;
import br.com.fullcycle.hexagonal.application.repositories.TicketRepository;

class SubscribeCustomerToEventUseCaseTest {
    private SubscribeCustomerToEventUseCase useCase;
    private CustomerRepository customerRepository;
    private EventRepository eventRepository;
    private TicketRepository ticketRepository;

    SubscribeCustomerToEventUseCaseTest() {
        customerRepository = new InMemoryCustomerRepository();
        eventRepository = new InMemoryEventRepository();
        ticketRepository = new InMemoryTicketRepository();
        useCase = new SubscribeCustomerToEventUseCase(
                customerRepository,
                eventRepository,
                ticketRepository);
    }

    @AfterEach
    void tearDown() {
        ticketRepository.deleteAll();
        eventRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve comprar um ticket de um evento")
    public void testReserveTicket() throws Exception {
        // given
        final var expectedTicketsSize = 1;
        final var customer = Customer.newCustomer("John Doe", "123.456.789-01", "john.doe@gmail.com");
        final var partner = Partner.newPartner("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");
        final var event = Event.newEvent("Disney", "2024-08-01", 10, partner);

        customerRepository.create(customer);
        eventRepository.create(event);

        final var input = new SubscribeCustomerToEventUseCase.Input(event.id().value(), customer.id().value());

        // when
        final var output = useCase.execute(input);
        final var updatedEvent = eventRepository.getById(event.id());

        // then
        Assertions.assertEquals(event.id().value(), output.eventId());
        Assertions.assertNotNull(output.reservationDate());
        Assertions.assertEquals(TicketStatus.PENDING.name(), output.ticketStatus());
        Assertions.assertEquals(expectedTicketsSize, updatedEvent.get().allTickets().size());
    }

    @Test
    @DisplayName("Não deve comprar um ticket de um evento que não existe")
    public void testReserveTicketWithoutEvent() throws Exception {
        // given
        final var expectedError = "Event not found";

        final var customer = Customer.newCustomer("John Doe", "123.456.789-01", "john.doe@gmail.com");
        final var eventId = EventId.unique().value();

        customerRepository.create(customer);

        final var input = new SubscribeCustomerToEventUseCase.Input(eventId, customer.id().value());

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
        final var partner = Partner.newPartner("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");
        final var event = Event.newEvent("Disney", "2024-08-01", 10, partner);

        eventRepository.create(event);

        final var input = new SubscribeCustomerToEventUseCase.Input(event.id().value(), customerId);

        // when
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(input));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Um mesmo cliente não pode comprar mais de um ticket por evento")
    public void testReserveTicketMoreThanOnce() throws Exception {
        // given
        final var expectedError = "Email already registered";

        final var customer = Customer.newCustomer("John Doe", "123.456.789-01", "john.doe@gmail.com");
        final var partner = Partner.newPartner("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");
        final var event = Event.newEvent("Disney", "2024-08-01", 10, partner);
        final var ticket = event.reserveTicket(customer.id());

        customerRepository.create(customer);
        eventRepository.create(event);
        ticketRepository.create(ticket);

        final var input = new SubscribeCustomerToEventUseCase.Input(event.id().value(), customer.id().value());

        // when
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(input));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Um cliente não pode comprar de um evento que não há mais lugares")
    public void testReserveTicketWithoutSpots() throws Exception {
        // given
        final var expectedError = "Event sold out";

        final var customer = Customer.newCustomer("John Doe", "123.456.789-01", "john.doe@gmail.com");
        final var anotherCustomer = Customer.newCustomer("Jane Doe", "123.456.789-02", "jane.doe@gmail.com");
        final var partner = Partner.newPartner("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");
        final var event = Event.newEvent("Disney", "2024-08-01", 1, partner);
        final var ticket = event.reserveTicket(anotherCustomer.id());

        customerRepository.create(customer);
        customerRepository.create(anotherCustomer);
        eventRepository.create(event);
        ticketRepository.create(ticket);

        final var input = new SubscribeCustomerToEventUseCase.Input(event.id().value(), customer.id().value());

        // when
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(input));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }
}
