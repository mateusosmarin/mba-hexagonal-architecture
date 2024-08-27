package br.com.fullcycle.hexagonal.application.repositories;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import br.com.fullcycle.hexagonal.application.domain.Cpf;
import br.com.fullcycle.hexagonal.application.domain.Customer;
import br.com.fullcycle.hexagonal.application.domain.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.Email;

public class InMemoryCustomerRepository implements CustomerRepository {
    private final Map<String, Customer> customers;
    private final Map<String, Customer> customersByCPF;
    private final Map<String, Customer> customersByEmail;

    public InMemoryCustomerRepository() {
        this.customers = new HashMap<>();
        this.customersByCPF = new HashMap<>();
        this.customersByEmail = new HashMap<>();
    }

    public Optional<Customer> getById(CustomerId id) {
        return Optional.ofNullable(this.customers.get(Objects.requireNonNull(id).value()));
    }

    public Optional<Customer> getByCpf(Cpf cpf) {
        return Optional.ofNullable(this.customersByCPF.get(Objects.requireNonNull(cpf.value())));
    }

    public Optional<Customer> getByEmail(Email email) {
        return Optional.ofNullable(this.customersByEmail.get(Objects.requireNonNull(email.value())));
    }

    public Customer create(Customer customer) {
        this.customers.put(customer.id().value(), customer);
        this.customersByCPF.put(customer.cpf().value(), customer);
        this.customersByEmail.put(customer.email().value(), customer);
        return customer;
    }

    public Customer update(Customer customer) {
        final var oldCustomer = this.customers.get(customer.id().value());
        this.customersByCPF.remove(oldCustomer.cpf().value());
        this.customersByEmail.remove(oldCustomer.email().value());

        this.customers.put(customer.id().value(), customer);
        this.customersByCPF.put(customer.cpf().value(), customer);
        this.customersByEmail.put(customer.email().value(), customer);

        return customer;
    }

    public void deleteAll() {
        this.customers.clear();
        this.customersByCPF.clear();
        this.customersByEmail.clear();
    }
}
