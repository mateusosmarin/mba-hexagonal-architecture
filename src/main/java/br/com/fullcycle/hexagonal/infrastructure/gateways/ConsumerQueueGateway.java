package br.com.fullcycle.hexagonal.infrastructure.gateways;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fullcycle.hexagonal.application.domain.event.EventTicketReserved;
import br.com.fullcycle.hexagonal.application.usecases.ticket.CreateTicketForCustomerUseCase;

@Component
public class ConsumerQueueGateway implements QueueGateway {
    private final CreateTicketForCustomerUseCase createTicketForCustomerUseCase;
    private final ObjectMapper mapper;

    public ConsumerQueueGateway(final CreateTicketForCustomerUseCase createTicketForCustomerUseCase,
            final ObjectMapper mapper) {
        this.createTicketForCustomerUseCase = createTicketForCustomerUseCase;
        this.mapper = mapper;
    }

    @Async(value = "queueExecutor")
    @Override
    public void publish(final String message) {
        if (message == null) {
            return;
        }
        if (message.contains("event-ticket.reserved")) {
            final var dto = safeRead(message, EventTicketReserved.class);
            final var input = new CreateTicketForCustomerUseCase.Input(
                    dto.eventTicketId(),
                    dto.eventId(),
                    dto.customerId());
            this.createTicketForCustomerUseCase.execute(input);
        }
    }

    private <T> T safeRead(final String message, final Class<T> tClass) {
        try {
            return this.mapper.readValue(message, tClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
