package br.com.fullcycle.hexagonal.application.domain;

import java.util.Objects;

public class Partner {
    private final PartnerId id;
    private Name name;
    private Cnpj cnpj;
    private Email email;

    public Partner(final PartnerId id, final String name, final String cnpj, final String email) {
        this.id = id;
        this.setName(name);
        this.setCnpj(cnpj);
        this.setEmail(email);
    }

    public static Partner newPartner(final String name, final String cnpj, final String email) {
        return new Partner(PartnerId.unique(), name, cnpj, email);
    }

    public PartnerId id() {
        return id;
    }

    public Name name() {
        return name;
    }

    private void setName(final String name) {
        this.name = new Name(name);
    }

    public Cnpj cnpj() {
        return cnpj;
    }

    private void setCnpj(final String cnpj) {
        this.cnpj = new Cnpj(cnpj);
    }

    public Email email() {
        return email;
    }

    private void setEmail(final String email) {
        this.email = new Email(email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Partner partner = (Partner) o;
        return Objects.equals(id, partner.id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
