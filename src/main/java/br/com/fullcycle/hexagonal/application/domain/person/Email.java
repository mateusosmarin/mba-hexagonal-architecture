package br.com.fullcycle.hexagonal.application.domain.person;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

public record Email(String value) {
    public Email {
        if (value == null || !value.matches("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w+)+$")) {
            throw new ValidationException("Invalid value for Email");
        }
    }
}
