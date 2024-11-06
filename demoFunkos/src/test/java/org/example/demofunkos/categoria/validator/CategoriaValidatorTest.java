package org.example.demofunkos.categoria.validator;

import org.example.demofunkos.categoria.models.Categoria;
import org.example.demofunkos.categoria.repositories.CategoriaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaValidatorTest {

    @Mock
    private CategoriaRepository repository;

    @InjectMocks
    private CategoriaValidator categoriaValidator;

    @Test
    void isNameUnique() {
        String nombre = "CategoriaNueva";

        when(repository.findByNombre(nombre)).thenReturn(Optional.empty());

        boolean result = categoriaValidator.isNameUnique(nombre);

        assertTrue(result);

        verify(repository, times(1)).findByNombre(nombre);
    }

    @Test
    void isNameUniqueFalse() {
        String nombre = "CategoriaExistente";

        when(repository.findByNombre(nombre)).thenReturn(Optional.of(new Categoria()));

        boolean result = categoriaValidator.isNameUnique(nombre);

        assertFalse(result);

        verify(repository, times(1)).findByNombre(nombre);
    }
}