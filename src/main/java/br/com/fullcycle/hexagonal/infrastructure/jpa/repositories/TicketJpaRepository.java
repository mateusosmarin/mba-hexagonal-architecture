package br.com.fullcycle.hexagonal.infrastructure.jpa.repositories;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import br.com.fullcycle.hexagonal.infrastructure.jpa.models.TicketJpaEntity;

public interface TicketJpaRepository extends CrudRepository<TicketJpaEntity, UUID> {
}
