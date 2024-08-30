package br.com.fullcycle.hexagonal.infrastructure.jobs;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.fullcycle.hexagonal.infrastructure.gateways.QueueGateway;
import br.com.fullcycle.hexagonal.infrastructure.jpa.repositories.OutboxJpaRepository;

@Component
public class OutboxRelay {
    private final OutboxJpaRepository outboxJpaRepository;
    private final QueueGateway queueGateway;

    public OutboxRelay(final OutboxJpaRepository outboxJpaRepository, final QueueGateway queueGateway) {
        this.outboxJpaRepository = outboxJpaRepository;
        this.queueGateway = queueGateway;
    }

    @Scheduled(fixedRate = 2_000)
    @Transactional
    void execute() {
        outboxJpaRepository.findTop100ByPublishedFalse().forEach(it -> {
            queueGateway.publish(it.getContent());
            outboxJpaRepository.save(it.notePublished());
        });
    }
}
