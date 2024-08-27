package br.com.fullcycle.hexagonal.application.usecases;

import java.util.Optional;

import br.com.fullcycle.hexagonal.application.UseCase;
import br.com.fullcycle.hexagonal.application.domain.CustomerId;
import br.com.fullcycle.hexagonal.application.repositories.CustomerRepository;

public class GetCustomerByIdUseCase
        extends UseCase<GetCustomerByIdUseCase.Input, Optional<GetCustomerByIdUseCase.Output>> {
    public record Input(String id) {
    }

    public record Output(String id, String cpf, String email, String name) {
    }

    private final CustomerRepository customerRepository;

    public GetCustomerByIdUseCase(final CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Optional<Output> execute(final Input input) {
        return customerRepository.getById(CustomerId.with(input.id()))
                .map(c -> new Output(
                        c.id().value(),
                        c.cpf().value(),
                        c.email().value(),
                        c.name().value()));
    }
}
