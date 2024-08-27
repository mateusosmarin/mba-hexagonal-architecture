package br.com.fullcycle.hexagonal.infrastructure.repositories;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.fullcycle.hexagonal.application.domain.Cnpj;
import br.com.fullcycle.hexagonal.application.domain.Partner;
import br.com.fullcycle.hexagonal.application.domain.PartnerId;
import br.com.fullcycle.hexagonal.application.domain.Email;
import br.com.fullcycle.hexagonal.application.repositories.PartnerRepository;
import br.com.fullcycle.hexagonal.infrastructure.jpa.models.PartnerJpaEntity;
import br.com.fullcycle.hexagonal.infrastructure.jpa.repositories.PartnerJpaRepository;

@Component
public class PartnerDatabaseRepository implements PartnerRepository {
    private final PartnerJpaRepository partnerJpaRepository;

    public PartnerDatabaseRepository(final PartnerJpaRepository partnerJpaRepository) {
        this.partnerJpaRepository = partnerJpaRepository;
    }

    @Override
    public Optional<Partner> getById(PartnerId id) {
        Objects.requireNonNull(id, "id cannot be null");
        return this.partnerJpaRepository
                .findById(UUID.fromString(id.value()))
                .map(it -> it.toPartner());
    }

    @Override
    public Optional<Partner> getByCnpj(Cnpj cnpj) {
        Objects.requireNonNull(cnpj, "cnpj cannot be null");
        return this.partnerJpaRepository
                .findByCnpj(cnpj.value())
                .map(it -> it.toPartner());
    }

    @Override
    public Optional<Partner> getByEmail(Email email) {
        Objects.requireNonNull(email, "email cannot be null");
        return this.partnerJpaRepository
                .findByEmail(email.value())
                .map(it -> it.toPartner());
    }

    @Override
    @Transactional
    public Partner create(Partner partner) {
        return this.partnerJpaRepository.save(PartnerJpaEntity.of(partner)).toPartner();
    }

    @Override
    @Transactional
    public Partner update(Partner partner) {
        return this.partnerJpaRepository.save(PartnerJpaEntity.of(partner)).toPartner();
    }

    public void deleteAll() {
        this.partnerJpaRepository.deleteAll();
    }
}
