package br.com.fullcycle.hexagonal.application.repositories;

import java.util.Optional;

import br.com.fullcycle.hexagonal.application.domain.Cnpj;
import br.com.fullcycle.hexagonal.application.domain.Email;
import br.com.fullcycle.hexagonal.application.domain.Partner;
import br.com.fullcycle.hexagonal.application.domain.PartnerId;

public interface PartnerRepository {
    Optional<Partner> getById(PartnerId id);

    Optional<Partner> getByCnpj(Cnpj cnpj);

    Optional<Partner> getByEmail(Email email);

    Partner create(Partner partner);

    Partner update(Partner partner);
    
    void deleteAll();
}
