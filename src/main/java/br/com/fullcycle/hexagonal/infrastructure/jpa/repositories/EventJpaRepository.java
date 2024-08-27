package br.com.fullcycle.hexagonal.infrastructure.jpa.repositories;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import br.com.fullcycle.hexagonal.infrastructure.jpa.models.EventJpaEntity;

public interface EventJpaRepository extends CrudRepository<EventJpaEntity, UUID> {

}
