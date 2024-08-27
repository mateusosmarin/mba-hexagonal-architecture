package br.com.fullcycle.hexagonal.infrastructure.jpa.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import br.com.fullcycle.hexagonal.infrastructure.jpa.models.PartnerJpaEntity;

public interface PartnerJpaRepository extends CrudRepository<PartnerJpaEntity, UUID> {

    Optional<PartnerJpaEntity> findByCnpj(String cnpj);

    Optional<PartnerJpaEntity> findByEmail(String email);
}
