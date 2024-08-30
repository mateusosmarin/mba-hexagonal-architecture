package br.com.fullcycle.hexagonal.application.usecases.partner;

import br.com.fullcycle.hexagonal.application.usecases.UseCase;
import br.com.fullcycle.hexagonal.application.domain.person.Cnpj;
import br.com.fullcycle.hexagonal.application.domain.person.Email;
import br.com.fullcycle.hexagonal.application.domain.partner.Partner;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.repositories.PartnerRepository;

public class CreatePartnerUseCase extends UseCase<CreatePartnerUseCase.Input, CreatePartnerUseCase.Output> {
    public record Input(String cnpj, String email, String name) {
    }

    public record Output(String id, String cnpj, String email, String name) {
    }

    private final PartnerRepository partnerRepository;

    public CreatePartnerUseCase(final PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    @Override
    public Output execute(final Input input) {
        if (partnerRepository.getByCnpj(new Cnpj(input.cnpj())).isPresent()) {
            throw new ValidationException("Partner already exists");
        }
        if (partnerRepository.getByEmail(new Email(input.email())).isPresent()) {
            throw new ValidationException("Partner already exists");
        }

        final var partner = partnerRepository.create(Partner.newPartner(input.name(), input.cnpj(), input.email()));

        return new Output(
                partner.id().value(),
                partner.cnpj().value(),
                partner.email().value(),
                partner.name().value());
    }
}
