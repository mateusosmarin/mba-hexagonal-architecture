package br.com.fullcycle.hexagonal.application.domain.event;

import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.fullcycle.hexagonal.application.domain.customer.Customer;
import br.com.fullcycle.hexagonal.application.domain.partner.Partner;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

class EventTest {
    @Test
    @DisplayName("Deve instanciar um evento")
    public void testCreate() {
        // given
        final var partner = Partner.newPartner("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");

        final var expectedDate = "2024-08-01";
        final var expectedName = "Frozen";
        final var expectedTotalSpots = 10;
        final var expectedTickets = 0;
        final var expectedPartnerId = partner.id().value();

        // when
        final var event = Event.newEvent(expectedName, expectedDate, expectedTotalSpots, partner);

        // then
        Assertions.assertNotNull(event.id());
        Assertions.assertEquals(expectedDate, event.date().format(DateTimeFormatter.ISO_LOCAL_DATE));
        Assertions.assertEquals(expectedName, event.name().value());
        Assertions.assertEquals(expectedTotalSpots, event.totalSpots());
        Assertions.assertEquals(expectedTickets, event.allTickets().size());
        Assertions.assertEquals(expectedPartnerId, event.partnerId().value());
    }

    @Test
    @DisplayName("Não deve instanciar um evento com nome inválido")
    public void testCreateWithInvalidName() {
        // given
        final var partner = Partner.newPartner("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");

        final var expectedError = "Invalid value for Name";

        // when
        final var actualException = Assertions.assertThrows(ValidationException.class,
                () -> Event.newEvent(null, "2024-08-01", 10, partner));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve instanciar um evento com data inválida")
    public void testCreateWithInvalidDate() {
        // given
        final var partner = Partner.newPartner("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");

        final var expectedError = "Invalid date for Event";

        // when
        final var actualException = Assertions.assertThrows(ValidationException.class,
                () -> Event.newEvent("Frozen", "2024/08/01", 10, partner));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Deve reservar um ticket")
    public void testReserveTicket() {
        // given
        final var expectedTickets = 1;
        final var expectedTicketOrder = 1;
        final var expectedDomainEvent = "event-ticket.reserved";

        final var customer = Customer.newCustomer("John Doe", "123.456.789-01", "john.doe@gmail.com");
        final var partner = Partner.newPartner("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");
        final var event = Event.newEvent("Frozen", "2024-08-01", 10, partner);

        // when
        final var ticket = event.reserveTicket(customer.id());

        // then
        Assertions.assertNotNull(ticket.id());
        Assertions.assertNull(ticket.ticketId());
        Assertions.assertEquals(event.id(), ticket.eventId());
        Assertions.assertEquals(customer.id(), ticket.customerId());
        Assertions.assertEquals(expectedTickets, event.allTickets().size());

        final var eventTicket = event.allTickets().iterator().next();
        Assertions.assertNull(eventTicket.ticketId());
        Assertions.assertEquals(expectedTicketOrder, eventTicket.ordering());

        final var domainEvent = event.allDomainEvents().iterator().next();
        Assertions.assertEquals(domainEvent.type(), expectedDomainEvent);
    }

    @Test
    @DisplayName("Não deve reservar um ticket quando o evento está esgotado")
    public void testReserveTicketWhenEventIsSoldOut() {
        // given
        final var expectedError = "Event sold out";

        final var customer = Customer.newCustomer("John Doe", "123.456.789-01", "john.doe@gmail.com");
        final var anotherCustomer = Customer.newCustomer("Jane Doe", "123.456.789-02", "jane.doe@gmail.com");
        final var partner = Partner.newPartner("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");
        final var event = Event.newEvent("Frozen", "2024-08-01", 1, partner);

        event.reserveTicket(anotherCustomer.id());

        // when
        final var actualException = Assertions.assertThrows(ValidationException.class,
                () -> event.reserveTicket(customer.id()));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve reservar mais de um ticket por cliente")
    public void testReserveTicketForTheSameCustomer() {
        // given
        final var expectedError = "Email already registered";

        final var customer = Customer.newCustomer("John Doe", "123.456.789-01", "john.doe@gmail.com");
        final var partner = Partner.newPartner("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");
        final var event = Event.newEvent("Frozen", "2024-08-01", 1, partner);

        event.reserveTicket(customer.id());

        // when
        final var actualException = Assertions.assertThrows(ValidationException.class,
                () -> event.reserveTicket(customer.id()));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }
}
