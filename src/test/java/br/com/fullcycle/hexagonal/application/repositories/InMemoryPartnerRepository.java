package br.com.fullcycle.hexagonal.application.repositories;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import br.com.fullcycle.hexagonal.application.domain.person.Cnpj;
import br.com.fullcycle.hexagonal.application.domain.person.Email;
import br.com.fullcycle.hexagonal.application.domain.partner.Partner;
import br.com.fullcycle.hexagonal.application.domain.partner.PartnerId;

public class InMemoryPartnerRepository implements PartnerRepository {
    private final Map<String, Partner> partners;
    private final Map<String, Partner> partnersByCNPJ;
    private final Map<String, Partner> partnersByEmail;

    public InMemoryPartnerRepository() {
        this.partners = new HashMap<>();
        this.partnersByCNPJ = new HashMap<>();
        this.partnersByEmail = new HashMap<>();
    }

    public Optional<Partner> getById(PartnerId id) {
        return Optional.ofNullable(this.partners.get(Objects.requireNonNull(id).value()));
    }

    public Optional<Partner> getByCnpj(Cnpj cnpj) {
        return Optional.ofNullable(this.partnersByCNPJ.get(Objects.requireNonNull(cnpj).value()));
    }

    public Optional<Partner> getByEmail(Email email) {
        return Optional.ofNullable(this.partnersByEmail.get(Objects.requireNonNull(email).value()));
    }

    public Partner create(Partner partner) {
        this.partners.put(partner.id().value(), partner);
        this.partnersByCNPJ.put(partner.cnpj().value(), partner);
        this.partnersByEmail.put(partner.email().value(), partner);
        return partner;
    }

    public Partner update(Partner partner) {
        final var oldPartner = this.partners.get(partner.id().value());
        this.partnersByCNPJ.remove(oldPartner.cnpj().value());
        this.partnersByEmail.remove(oldPartner.email().value());

        this.partners.put(partner.id().value(), partner);
        this.partnersByCNPJ.put(partner.cnpj().value(), partner);
        this.partnersByEmail.put(partner.email().value(), partner);

        return partner;
    }

    public void deleteAll() {
        this.partners.clear();
        this.partnersByCNPJ.clear();
        this.partnersByEmail.clear();
    }
}
