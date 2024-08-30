package br.com.fullcycle.hexagonal.application.usecases.customer;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.fullcycle.hexagonal.IntegrationTest;
import br.com.fullcycle.hexagonal.application.domain.customer.Customer;
import br.com.fullcycle.hexagonal.application.repositories.CustomerRepository;

class GetCustomerByIdUseCaseIT extends IntegrationTest {
    @Autowired
    private GetCustomerByIdUseCase useCase;

    @Autowired
    private CustomerRepository customerRepository;

    @AfterEach
    public void tearDown() {
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve obter um cliente por id")
    public void testGetById() {
        // given
        final var expectedCPF = "123.456.789-01";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        final var customer = createCustomer(expectedCPF, expectedEmail, expectedName);
        final var expectedId = customer.id().value();

        final var input = new GetCustomerByIdUseCase.Input(expectedId);

        // when
        final var output = useCase.execute(input).get();

        // then
        Assertions.assertEquals(expectedId, output.id());
        Assertions.assertEquals(expectedCPF, output.cpf());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedName, output.name());

    }

    @Test
    @DisplayName("Deve obter vazio ao tentar recuperar um cliente não existente por id")
    public void testGetByIdWithInvalidId() {
        // given
        final var expectedId = UUID.randomUUID().toString();

        final var input = new GetCustomerByIdUseCase.Input(expectedId);

        // when
        final var output = useCase.execute(input);

        // then
        Assertions.assertTrue(output.isEmpty());
    }

    private Customer createCustomer(final String cpf, final String email, final String name) {
        return customerRepository.create(Customer.newCustomer(name, cpf, email));
    }
}
