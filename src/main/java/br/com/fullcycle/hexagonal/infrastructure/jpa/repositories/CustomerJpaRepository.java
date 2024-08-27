package br.com.fullcycle.hexagonal.infrastructure.jpa.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import br.com.fullcycle.hexagonal.infrastructure.jpa.models.CustomerJpaEntity;

public interface CustomerJpaRepository extends CrudRepository<CustomerJpaEntity, UUID> {

    Optional<CustomerJpaEntity> findByCpf(String cpf);

    Optional<CustomerJpaEntity> findByEmail(String email);
}
