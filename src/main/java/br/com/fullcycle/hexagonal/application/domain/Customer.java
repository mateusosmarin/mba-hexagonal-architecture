package br.com.fullcycle.hexagonal.application.domain;

import java.util.Objects;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

public class Customer {
    private final CustomerId id;
    private Name name;
    private Cpf cpf;
    private Email email;

    public Customer(final CustomerId id, final String name, final String cpf, final String email) {
        if (id == null) {
            throw new ValidationException("Invalid id for Customer");
        }
        this.id = id;
        setName(name);
        setCpf(cpf);
        setEmail(email);
    }

    public static Customer newCustomer(final String name, final String cpf, final String email) {
        return new Customer(CustomerId.unique(), name, cpf, email);
    }

    public CustomerId id() {
        return id;
    }

    public Name name() {
        return name;
    }

    private void setName(final String name) {
        this.name = new Name(name);
    }

    public Cpf cpf() {
        return cpf;
    }

    private void setCpf(final String cpf) {
        this.cpf = new Cpf(cpf);
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
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
