package org.example.demofunkos.categoria.validator;

import org.example.demofunkos.categoria.repositories.CategoriaRepository;
import org.springframework.stereotype.Component;

@Component
public class CategoriaValidator {
    private final CategoriaRepository categoriaRepository;

    public CategoriaValidator(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public boolean isNameUnique(String nombre) {
        return categoriaRepository.findByNombre(nombre).isEmpty();
    }
}
