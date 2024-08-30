package br.com.fullcycle.hexagonal.infrastructure.repositories;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.fullcycle.hexagonal.application.domain.person.Cpf;
import br.com.fullcycle.hexagonal.application.domain.customer.Customer;
import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.person.Email;
import br.com.fullcycle.hexagonal.application.repositories.CustomerRepository;
import br.com.fullcycle.hexagonal.infrastructure.jpa.models.CustomerJpaEntity;
import br.com.fullcycle.hexagonal.infrastructure.jpa.repositories.CustomerJpaRepository;

@Component
public class CustomerDatabaseRepository implements CustomerRepository {
    private final CustomerJpaRepository customerJpaRepository;

    public CustomerDatabaseRepository(final CustomerJpaRepository customerJpaRepository) {
        this.customerJpaRepository = customerJpaRepository;
    }

    @Override
    public Optional<Customer> getById(CustomerId id) {
        Objects.requireNonNull(id, "id cannot be null");
        return this.customerJpaRepository
                .findById(UUID.fromString(id.value()))
                .map(it -> it.toCustomer());
    }

    @Override
    public Optional<Customer> getByCpf(Cpf cpf) {
        Objects.requireNonNull(cpf, "cpf cannot be null");
        return this.customerJpaRepository
                .findByCpf(cpf.value())
                .map(it -> it.toCustomer());
    }

    @Override
    public Optional<Customer> getByEmail(Email email) {
        Objects.requireNonNull(email, "email cannot be null");
        return this.customerJpaRepository
                .findByEmail(email.value())
                .map(it -> it.toCustomer());
    }

    @Override
    @Transactional
    public Customer create(Customer customer) {
        return this.customerJpaRepository.save(CustomerJpaEntity.of(customer)).toCustomer();
    }

    @Override
    @Transactional
    public Customer update(Customer customer) {
        return this.customerJpaRepository.save(CustomerJpaEntity.of(customer)).toCustomer();
    }

    public void deleteAll() {
        this.customerJpaRepository.deleteAll();
    }
}
