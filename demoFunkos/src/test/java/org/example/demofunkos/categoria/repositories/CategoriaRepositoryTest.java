package org.example.demofunkos.categoria.repositories;

import org.example.demofunkos.categoria.models.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Categoria categoriaTest;

    @BeforeEach
    void setUp() {
        categoriaTest = new Categoria();
        categoriaTest.setId(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"));
        categoriaTest.setNombre("DISNEY");
        categoriaTest.setActivado(true);
        entityManager.merge(categoriaTest);
        entityManager.flush();
    }

    @Test
    void findById() {
        var result = repository.findById(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"));

        assertAll(
                () -> assertTrue(result.isPresent()),
                () -> {
                    if (result.isPresent()) {
                        assertEquals("DISNEY", result.get().getNombre());
                        assertTrue(result.get().getActivado());
                    }
                }
        );
    }

    @Test
    void findByIdAndActivadoTrue() {
        var result = repository.findByIdAndActivadoTrue(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"));

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("DISNEY", result.get().getNombre()),
                () -> assertTrue(result.get().getActivado())
        );
    }

    @Test
    void findByNombre() {
        var result = repository.findByNombre("DISNEY");

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("DISNEY", result.get().getNombre()),
                () -> assertTrue(result.get().getActivado())
        );
    }
}