package br.com.fullcycle.hexagonal.infrastructure.jpa.models;

import java.util.Objects;
import java.util.UUID;

import br.com.fullcycle.hexagonal.application.domain.Customer;
import br.com.fullcycle.hexagonal.application.domain.CustomerId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "Customer")
@Table(name = "customers")
public class CustomerJpaEntity {

    @Id
    private UUID id;

    private String name;

    private String cpf;

    private String email;

    public CustomerJpaEntity() {
    }

    public CustomerJpaEntity(UUID id, String name, String cpf, String email) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CustomerJpaEntity customer = (CustomerJpaEntity) o;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Customer toCustomer() {
        return new Customer(CustomerId.with(id.toString()), name, cpf, email);
    }

    public static CustomerJpaEntity of(Customer customer) {
        return new CustomerJpaEntity(
                UUID.fromString(customer.id().value()),
                customer.name().value(),
                customer.cpf().value(),
                customer.email().value());
    }
}
