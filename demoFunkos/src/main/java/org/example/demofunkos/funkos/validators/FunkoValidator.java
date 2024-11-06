package org.example.demofunkos.funkos.validators;

import org.example.demofunkos.funkos.repositories.FunkoRepository;
import org.springframework.stereotype.Component;

@Component
public class FunkoValidator {

    private final FunkoRepository funkoRepository;

    public FunkoValidator(FunkoRepository funkoRepository) {
        this.funkoRepository = funkoRepository;
    }

    public boolean isNameUnique(String nombre) {
        return funkoRepository.findByNombre(nombre).isEmpty();
    }

    public boolean isIdValid(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}