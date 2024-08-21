package br.com.fullcycle.hexagonal.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.usecases.CreateCustomerUseCase;
import br.com.fullcycle.hexagonal.application.usecases.GetCustomerByIdUseCase;
import br.com.fullcycle.hexagonal.dtos.CustomerDTO;
import br.com.fullcycle.hexagonal.services.CustomerService;

// Adapter
@RestController
@RequestMapping(value = "customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody final CustomerDTO dto) {
        try {
            final var useCase = new CreateCustomerUseCase(customerService);
            final var input = new CreateCustomerUseCase.Input(dto.getCpf(), dto.getEmail(), dto.getName());
            final var output = useCase.execute(input);
            return ResponseEntity.created(URI.create("/customers/" + output.id())).body(output);
        } catch (final ValidationException ex) {
            return ResponseEntity.unprocessableEntity().body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable final Long id) {
        final var useCase = new GetCustomerByIdUseCase(customerService);
        final var input = new GetCustomerByIdUseCase.Input(id);
        return useCase.execute(input)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }
}
