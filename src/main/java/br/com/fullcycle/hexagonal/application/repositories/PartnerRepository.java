package br.com.fullcycle.hexagonal.application.repositories;

import java.util.Optional;

import br.com.fullcycle.hexagonal.application.domain.person.Cnpj;
import br.com.fullcycle.hexagonal.application.domain.person.Email;
import br.com.fullcycle.hexagonal.application.domain.partner.Partner;
import br.com.fullcycle.hexagonal.application.domain.partner.PartnerId;

public interface PartnerRepository {
    Optional<Partner> getById(PartnerId id);

    Optional<Partner> getByCnpj(Cnpj cnpj);

    Optional<Partner> getByEmail(Email email);

    Partner create(Partner partner);

    Partner update(Partner partner);
    
    void deleteAll();
}
