package org.example.demofunkos.categoria.services;

import org.example.demofunkos.categoria.dto.CategoriaDto;
import org.example.demofunkos.categoria.mappers.CategoriaMapper;
import org.example.demofunkos.categoria.models.Categoria;
import org.example.demofunkos.categoria.repositories.CategoriaRepository;
import org.example.demofunkos.categoria.validator.CategoriaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceImplTest {
    @Mock
    private CategoriaRepository repository;

    @Mock
    private CategoriaMapper mapper;

    @Mock
    private CategoriaValidator validator;

    @InjectMocks
    private CategoriaServiceImpl service;

    private Categoria categoriaTest;

    @BeforeEach
    void setUp() {
        categoriaTest = new Categoria();
        categoriaTest.setId(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"));
        categoriaTest.setNombre("DISNEY");
        categoriaTest.setActivado(true);
    }

    @Test
    void getAll() {
        when(service.getAll()).thenReturn(List.of(categoriaTest));

        var result = service.getAll();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertTrue(result.contains(categoriaTest)),
                () -> assertEquals("DISNEY", result.get(0).getNombre()),
                () -> assertTrue(result.get(0).getActivado())
        );

        verify(repository, times(1)).findAll();
    }

    @Test
    void getById() {
        when(repository.findById(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"))).thenReturn(Optional.of(categoriaTest));

        var result = service.getById(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"));

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("DISNEY", result.getNombre()),
                () -> assertTrue(result.getActivado())
        );

        verify(repository, times(1)).findById(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"));
    }

    @Test
    void getByNombre() {
        when(repository.findByNombre("DISNEY")).thenReturn(Optional.ofNullable(categoriaTest));

        var result = service.getByNombre("DISNEY");

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("DISNEY", result.getNombre()),
                () -> assertTrue(result.getActivado())
        );

        verify(repository, times(1)).findByNombre("DISNEY");
    }

    @Test
    void save() {
        CategoriaDto nuevaCategoria = new CategoriaDto();
        nuevaCategoria.setNombre("DISNEY");
        nuevaCategoria.setActivado(true);

        Categoria categoria = new Categoria();
        categoria.setNombre(nuevaCategoria.getNombre());
        categoria.setActivado(nuevaCategoria.getActivado());

        when(validator.isNameUnique(nuevaCategoria.getNombre())).thenReturn(true);
        when(mapper.fromDto(nuevaCategoria)).thenReturn(categoria);
        when(repository.save(categoria)).thenReturn(categoria);

        var result = service.save(nuevaCategoria);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("DISNEY", result.getNombre()),
                () -> assertTrue(result.getActivado())
        );

        verify(repository, times(1)).save(categoria);
        verify(mapper, times(1)).fromDto(nuevaCategoria);
    }

    @Test
    void update() {
        UUID id = UUID.randomUUID();
        CategoriaDto updatedCategoria = new CategoriaDto();
        updatedCategoria.setId(id);
        updatedCategoria.setNombre("SUPERHEROES");
        updatedCategoria.setActivado(true);
    
        Categoria categoriaExistente = new Categoria();
        categoriaExistente.setId(id);
        categoriaExistente.setNombre("OLD_NAME");
        categoriaExistente.setActivado(true);
    
        Categoria updatedCategoriaEntity = new Categoria();
        updatedCategoriaEntity.setId(id);
        updatedCategoriaEntity.setNombre("SUPERHEROES");
        updatedCategoriaEntity.setActivado(true);
    
        when(repository.findById(id)).thenReturn(Optional.of(categoriaExistente));
        when(mapper.toCategoria(updatedCategoria, categoriaExistente)).thenReturn(updatedCategoriaEntity);
        when(repository.save(updatedCategoriaEntity)).thenReturn(updatedCategoriaEntity);

        var result = service.update(id, updatedCategoria);
    
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(id, result.getId()),
                () -> assertEquals("SUPERHEROES", result.getNombre()),
                () -> assertTrue(result.getActivado())
        );
    
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(updatedCategoriaEntity);
        verify(mapper, times(1)).toCategoria(updatedCategoria, categoriaExistente);
    }

    @Test
    void delete() {
        UUID id = UUID.fromString("79741172-6da6-47f1-9525-a6c83053f856");
        CategoriaDto categoriaBorrada = new CategoriaDto();
        categoriaBorrada.setId(id);
        categoriaBorrada.setNombre("SERIE");
        categoriaBorrada.setActivado(true);

        Categoria categoriaExistente = new Categoria();
        categoriaExistente.setId(id);
        categoriaExistente.setNombre("SERIE");
        categoriaExistente.setActivado(true);

        Categoria updatedCategoriaEntity = new Categoria();
        updatedCategoriaEntity.setId(id);
        updatedCategoriaEntity.setNombre("SERIE");
        updatedCategoriaEntity.setActivado(true);

        when(repository.findByIdAndActivadoTrue(id)).thenReturn(Optional.of(categoriaExistente));
        when(mapper.toCategoria(categoriaBorrada, categoriaExistente)).thenReturn(updatedCategoriaEntity);
        when(repository.save(updatedCategoriaEntity)).thenReturn(updatedCategoriaEntity);

        var result = service.delete(id, categoriaBorrada);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(id, result.getId()),
                () -> assertEquals("SERIE", result.getNombre()),
                () -> assertTrue(result.getActivado())
        );

        verify(repository, times(1)).findByIdAndActivadoTrue(id);
        verify(repository, times(1)).save(updatedCategoriaEntity);
        verify(mapper, times(1)).toCategoria(categoriaBorrada, categoriaExistente);
    }
}