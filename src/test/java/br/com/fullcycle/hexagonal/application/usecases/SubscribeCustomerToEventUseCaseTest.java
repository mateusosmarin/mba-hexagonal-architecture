package br.com.fullcycle.hexagonal.application.usecases;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.models.Customer;
import br.com.fullcycle.hexagonal.models.Event;
import br.com.fullcycle.hexagonal.models.Ticket;
import br.com.fullcycle.hexagonal.models.TicketStatus;
import br.com.fullcycle.hexagonal.services.CustomerService;
import br.com.fullcycle.hexagonal.services.EventService;
import io.hypersistence.tsid.TSID;

class SubscribeCustomerToEventUseCaseTest {
    @Test
    @DisplayName("Deve comprar um ticket de um evento")
    public void testReserveTicket() throws Exception {
        // given
        final var expectedTicketsSize = 1;

        final var aCustomer = new Customer();
        aCustomer.setId(TSID.fast().toLong());
        aCustomer.setCpf("12345678901");
        aCustomer.setEmail("john.doe@gmail.com");
        aCustomer.setName("John Doe");

        final var aEvent = new Event();
        aEvent.setId(TSID.fast().toLong());
        aEvent.setName("Disney");
        aEvent.setTotalSpots(10);

        final var input = new SubscribeCustomerToEventUseCase.Input(aEvent.getId(), aCustomer.getId());

        // when
        final var customerService = Mockito.mock(CustomerService.class);
        final var eventService = Mockito.mock(EventService.class);

        when(customerService.findById(aCustomer.getId())).thenReturn(Optional.of(aCustomer));
        when(eventService.findById(aEvent.getId())).thenReturn(Optional.of(aEvent));
        when(eventService.findTicketByEventIdAndCustomerId(aEvent.getId(), aCustomer.getId()))
                .thenReturn(Optional.empty());
        when(eventService.save(any())).thenAnswer(a -> {
            final var e = a.getArgument(0, Event.class);
            Assertions.assertEquals(expectedTicketsSize, e.getTickets().size());
            return e;
        });

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var output = useCase.execute(input);

        // then
        Assertions.assertEquals(aEvent.getId(), output.eventId());
        Assertions.assertNotNull(output.reservationDate());
        Assertions.assertEquals(TicketStatus.PENDING.name(), output.ticketStatus());
    }

    @Test
    @DisplayName("Não deve comprar um ticket de um evento que não existe")
    public void testReserveTicketWithoutEvent() throws Exception {
        // given
        final var expectedError = "Event not found";

        final var aCustomer = new Customer();
        aCustomer.setId(TSID.fast().toLong());
        aCustomer.setCpf("12345678901");
        aCustomer.setEmail("john.doe@gmail.com");
        aCustomer.setName("John Doe");

        final var aEvent = new Event();
        aEvent.setId(TSID.fast().toLong());
        aEvent.setName("Disney");
        aEvent.setTotalSpots(10);

        final var input = new SubscribeCustomerToEventUseCase.Input(aEvent.getId(), aCustomer.getId());

        // when
        final var customerService = Mockito.mock(CustomerService.class);
        final var eventService = Mockito.mock(EventService.class);

        when(customerService.findById(aCustomer.getId())).thenReturn(Optional.of(aCustomer));
        when(eventService.findById(aEvent.getId())).thenReturn(Optional.empty());

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(input));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve comprar um ticket com um cliente não existente")
    public void testReserveTicketWithoutCustomer() throws Exception {
        // given
        final var expectedError = "Customer not found";

        final var aCustomer = new Customer();
        aCustomer.setId(TSID.fast().toLong());
        aCustomer.setCpf("12345678901");
        aCustomer.setEmail("john.doe@gmail.com");
        aCustomer.setName("John Doe");

        final var aEvent = new Event();
        aEvent.setId(TSID.fast().toLong());
        aEvent.setName("Disney");
        aEvent.setTotalSpots(10);

        final var input = new SubscribeCustomerToEventUseCase.Input(aEvent.getId(), aCustomer.getId());

        // when
        final var customerService = Mockito.mock(CustomerService.class);
        final var eventService = Mockito.mock(EventService.class);

        when(customerService.findById(aCustomer.getId())).thenReturn(Optional.empty());

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(input));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Um mesmo cliente não pode comprar mais de um ticket por evento")
    public void testReserveTicketMoreThanOnce() throws Exception {
        // given
        final var expectedError = "Email already registered";

        final var aCustomer = new Customer();
        aCustomer.setId(TSID.fast().toLong());
        aCustomer.setCpf("12345678901");
        aCustomer.setEmail("john.doe@gmail.com");
        aCustomer.setName("John Doe");

        final var aEvent = new Event();
        aEvent.setId(TSID.fast().toLong());
        aEvent.setName("Disney");
        aEvent.setTotalSpots(10);

        final var input = new SubscribeCustomerToEventUseCase.Input(aEvent.getId(), aCustomer.getId());

        // when
        final var customerService = Mockito.mock(CustomerService.class);
        final var eventService = Mockito.mock(EventService.class);

        when(customerService.findById(aCustomer.getId())).thenReturn(Optional.of(aCustomer));
        when(eventService.findById(aEvent.getId())).thenReturn(Optional.of(aEvent));
        when(eventService.findTicketByEventIdAndCustomerId(aEvent.getId(), aCustomer.getId()))
                .thenReturn(Optional.of(new Ticket()));

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(input));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Um cliente não pode comprar de um evento que não há mais lugares")
    public void testReserveTicketWithoutSpots() throws Exception {
        // given
        final var expectedError = "Event sold out";

        final var aCustomer = new Customer();
        aCustomer.setId(TSID.fast().toLong());
        aCustomer.setCpf("12345678901");
        aCustomer.setEmail("john.doe@gmail.com");
        aCustomer.setName("John Doe");

        final var aEvent = new Event();
        aEvent.setId(TSID.fast().toLong());
        aEvent.setName("Disney");
        aEvent.setTotalSpots(0);

        final var input = new SubscribeCustomerToEventUseCase.Input(aEvent.getId(), aCustomer.getId());

        // when
        final var customerService = Mockito.mock(CustomerService.class);
        final var eventService = Mockito.mock(EventService.class);

        when(customerService.findById(aCustomer.getId())).thenReturn(Optional.of(aCustomer));
        when(eventService.findById(aEvent.getId())).thenReturn(Optional.of(aEvent));
        when(eventService.findTicketByEventIdAndCustomerId(aEvent.getId(), aCustomer.getId()))
                .thenReturn(Optional.empty());

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(input));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }
}
