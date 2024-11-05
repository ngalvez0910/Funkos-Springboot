package org.example.demofunkos.categoria.validator;

import org.example.demofunkos.categoria.models.TipoCategoria;
import org.example.demofunkos.categoria.repositories.CategoriaRepository;
import org.springframework.stereotype.Component;

@Component
public class CategoriaValidator {
    private CategoriaRepository repository;

    public CategoriaValidator(CategoriaRepository repository) {
        this.repository = repository;
    }

    public boolean categoriaValida(String nombre) {
        try {
            TipoCategoria.valueOf(nombre);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
