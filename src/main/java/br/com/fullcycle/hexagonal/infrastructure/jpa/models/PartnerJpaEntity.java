package br.com.fullcycle.hexagonal.infrastructure.jpa.models;

import java.util.UUID;

import br.com.fullcycle.hexagonal.application.domain.Partner;
import br.com.fullcycle.hexagonal.application.domain.PartnerId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "Partner")
@Table(name = "partners")
public class PartnerJpaEntity {

    @Id
    private UUID id;

    private String name;

    private String cnpj;

    private String email;

    public PartnerJpaEntity() {
    }

    public PartnerJpaEntity(UUID id, String name, String cnpj, String email) {
        this.id = id;
        this.name = name;
        this.cnpj = cnpj;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Partner toPartner() {
        return new Partner(PartnerId.with(id.toString()), name, cnpj, email);
    }

    public static PartnerJpaEntity of(Partner partner) {
        return new PartnerJpaEntity(
                UUID.fromString(partner.id().value()),
                partner.name().value(),
                partner.cnpj().value(),
                partner.email().value());
    }
}
