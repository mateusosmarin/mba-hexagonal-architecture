package br.com.fullcycle.hexagonal.infrastructure.controllers;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.usecases.partner.CreatePartnerUseCase;
import br.com.fullcycle.hexagonal.application.usecases.partner.GetPartnerByIdUseCase;
import br.com.fullcycle.hexagonal.infrastructure.dtos.CreatePartnerDTO;

@RestController
@RequestMapping(value = "partners")
public class PartnerController {
    private final CreatePartnerUseCase createPartnerUseCase;
    private final GetPartnerByIdUseCase getPartnerByIdUseCase;

    public PartnerController(
            final CreatePartnerUseCase createPartnerUseCase,
            final GetPartnerByIdUseCase getPartnerByIdUseCase) {
        this.createPartnerUseCase = createPartnerUseCase;
        this.getPartnerByIdUseCase = getPartnerByIdUseCase;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody final CreatePartnerDTO dto) {
        try {
            final var input = new CreatePartnerUseCase.Input(dto.cnpj(), dto.email(), dto.name());
            final var output = createPartnerUseCase.execute(input);
            return ResponseEntity.created(URI.create("/partners/" + output.id())).body(output);
        } catch (final ValidationException ex) {
            return ResponseEntity.unprocessableEntity().body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable final String id) {
        final var input = new GetPartnerByIdUseCase.Input(id);
        return getPartnerByIdUseCase.execute(input)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }
}
