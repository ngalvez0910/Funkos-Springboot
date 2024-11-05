package org.example.demofunkos.funkos.repositories;

import org.example.demofunkos.categoria.models.Categoria;
import org.example.demofunkos.funkos.models.Funko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FunkoRepositoryTest {

    @Autowired
    private FunkoRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private static final Categoria categoriaTest = new Categoria(UUID.randomUUID(), "PELICULA", LocalDateTime.now(), LocalDateTime.now(), true);
    private static final Funko funkoTest = new Funko(1L, "Darth Vader", 10.99, categoriaTest, LocalDateTime.now(), LocalDateTime.now());

    @BeforeEach
    void setUp() {
        entityManager.merge(categoriaTest);
        entityManager.flush();
        entityManager.merge(funkoTest);
        entityManager.flush();
    }

    @Test
    void findAll() {
        var result = repository.findAll();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(funkoTest.getNombre(), result.get(0).getNombre()),
                () -> assertEquals(funkoTest.getPrecio(), result.get(0).getPrecio()),
                () -> assertEquals(funkoTest.getCategoria(), result.get(0).getCategoria())
        );
    }

    @Test
    void findById() {
        var result = repository.findById(1L);

        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(funkoTest.getNombre(), result.get().getNombre()),
            () -> assertEquals(funkoTest.getPrecio(), result.get().getPrecio()),
            () -> assertEquals(funkoTest.getCategoria(), result.get().getCategoria())
        );
    }

    @Test
    void findByIdNotFound() {
        var result = repository.findById(999L);

        assertNull(result.orElse(null));
    }

    @Test
    void save() {
        var savedFunko = repository.save(funkoTest);

        assertAll(
                () -> assertNotNull(savedFunko.getId()),
                () -> assertEquals(funkoTest.getNombre(), savedFunko.getNombre()),
                () -> assertEquals(funkoTest.getPrecio(), savedFunko.getPrecio()),
                () -> assertEquals(funkoTest.getCategoria(), savedFunko.getCategoria())
        );
    }
}