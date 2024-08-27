package br.com.fullcycle.hexagonal.application.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TicketTest {
    @Test
    @DisplayName("Deve reservar um ticket")
    public void testReserveTicket() {
        // given
        final var expectedTicketStatus = TicketStatus.PENDING;

        final var customer = Customer.newCustomer("John Doe", "123.456.789-01", "john.doe@gmail.com");
        final var partner = Partner.newPartner("John Doe", "12.345.678/0001-00", "john.doe@gmail.com");
        final var event = Event.newEvent("Frozen", "2024-08-01", 10, partner);

        // when
        final var ticket = Ticket.newTicket(customer.id(), event.id());

        // then
        Assertions.assertNotNull(ticket.id());
        Assertions.assertEquals(event.id(), ticket.eventId());
        Assertions.assertEquals(customer.id(), ticket.customerId());
        Assertions.assertEquals(expectedTicketStatus, ticket.status());
        Assertions.assertNotNull(ticket.reservedAt());
        Assertions.assertNull(ticket.paidAt());
    }
}
