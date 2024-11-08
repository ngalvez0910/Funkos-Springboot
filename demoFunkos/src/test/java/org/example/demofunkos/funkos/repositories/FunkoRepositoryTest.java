package org.example.demofunkos.funkos.repositories;

import org.example.demofunkos.categoria.models.Categoria;
import org.example.demofunkos.funkos.models.Funko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FunkoRepositoryTest {

    @Autowired
    private FunkoRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private static Categoria categoriaTest = new Categoria(UUID.randomUUID(), "PELICULA", LocalDateTime.now(), LocalDateTime.now(), true);
    private static Funko funkoTest = new Funko(1L, "Darth Vader", 10.99, categoriaTest, LocalDateTime.now(), LocalDateTime.now());

    @BeforeEach
    void setUp() {
        categoriaTest = entityManager.merge(categoriaTest);
        entityManager.flush();
        funkoTest.setCategoria(categoriaTest);
        funkoTest = repository.saveAndFlush(funkoTest);
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
        Long id = funkoTest.getId();

        var result = repository.findById(id);

        assertAll(
                () -> assertTrue(result.isPresent()),
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

    @Test
    void findByNombre() {
        var result = repository.findByNombre("Darth Vader");

        assertAll(
                () -> assertEquals(funkoTest.getNombre(), result.get().getNombre()),
                () -> assertEquals(funkoTest.getPrecio(), result.get().getPrecio()),
                () -> assertEquals(funkoTest.getCategoria(), result.get().getCategoria())
        );
    }

    @Test
    void findByNombreNotFound() {
        var result = repository.findByNombre("FunkoTestNotFound");

        assertNull(result.orElse(null));
    }
}