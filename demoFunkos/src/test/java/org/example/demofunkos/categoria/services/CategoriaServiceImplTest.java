package org.example.demofunkos.categoria.services;

import org.example.demofunkos.categoria.dto.CategoriaDto;
import org.example.demofunkos.categoria.mappers.CategoriaMapper;
import org.example.demofunkos.categoria.models.Categoria;
import org.example.demofunkos.categoria.models.TipoCategoria;
import org.example.demofunkos.categoria.repositories.CategoriaRepository;
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

    @InjectMocks
    private CategoriaServiceImpl service;

    private Categoria categoriaTest;

    @BeforeEach
    void setUp() {
        categoriaTest = new Categoria();
        categoriaTest.setId(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"));
        categoriaTest.setNombre(TipoCategoria.DISNEY);
        categoriaTest.setActivado(true);
    }

    @Test
    void getAll() {
        when(service.getAll()).thenReturn(List.of(categoriaTest));

        var result = service.getAll();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertTrue(result.contains(categoriaTest)),
                () -> assertEquals(TipoCategoria.DISNEY, result.get(0).getNombre()),
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
                () -> assertEquals(TipoCategoria.DISNEY, result.getNombre()),
                () -> assertTrue(result.getActivado())
        );

        verify(repository, times(1)).findById(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"));
    }

    @Test
    void getByNombre() {
        when(repository.findByNombre(TipoCategoria.DISNEY)).thenReturn(categoriaTest);

        var result = service.getByNombre(TipoCategoria.DISNEY);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(TipoCategoria.DISNEY, result.getNombre()),
                () -> assertTrue(result.getActivado())
        );

        verify(repository, times(1)).findByNombre(TipoCategoria.DISNEY);
    }

    @Test
    void save() {
        CategoriaDto nuevaCategoria = new CategoriaDto();
        nuevaCategoria.setNombre(String.valueOf(TipoCategoria.DISNEY));
        nuevaCategoria.setActivado(true);

        Categoria categoria = new Categoria();
        categoria.setNombre(TipoCategoria.valueOf(nuevaCategoria.getNombre()));
        categoria.setActivado(nuevaCategoria.getActivado());

        when(mapper.fromDto(nuevaCategoria)).thenReturn(categoria);
        when(repository.save(categoria)).thenReturn(categoria);

        var result = service.save(nuevaCategoria);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(TipoCategoria.DISNEY, result.getNombre()),
                () -> assertTrue(result.getActivado())
        );

        verify(repository, times(1)).save(categoria);
        verify(mapper, times(1)).fromDto(nuevaCategoria);
    }

    @Test
    void update() {
        CategoriaDto updatedCategoria = new CategoriaDto();
        updatedCategoria.setNombre(String.valueOf(TipoCategoria.SUPERHEROES));
        updatedCategoria.setActivado(true);

        Categoria categoria = new Categoria();
        categoria.setNombre(TipoCategoria.valueOf(updatedCategoria.getNombre()));
        categoria.setActivado(updatedCategoria.getActivado());

        when(repository.findById(updatedCategoria.getId())).thenReturn(Optional.of(categoria));
        Categoria categoriaMapped = mapper.toCategoria(updatedCategoria, categoria);
        when(repository.save(categoriaMapped)).thenReturn(categoriaMapped);

        var result = service.update(updatedCategoria.getId(), updatedCategoria);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(updatedCategoria.getId(), result.getId()),
                () -> assertEquals(TipoCategoria.SUPERHEROES, result.getNombre()),
                () -> assertTrue(result.getActivado())
        );

        verify(repository, times(1)).findById(updatedCategoria.getId());
        verify(repository, times(1)).save(mapper.fromDto(updatedCategoria));
    }

    @Test
    void delete() {
        CategoriaDto nuevaCategoria = new CategoriaDto();
        nuevaCategoria.setId(UUID.randomUUID());
        nuevaCategoria.setNombre(String.valueOf(TipoCategoria.SERIE));
        nuevaCategoria.setActivado(true);

        when(repository.findById(nuevaCategoria.getId())).thenReturn(Optional.of(new Categoria()));

        var result = service.delete(nuevaCategoria.getId(), nuevaCategoria);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(nuevaCategoria.getNombre(), result.getNombre().toString()),
                () -> assertTrue(result.getActivado())
        );

        verify(repository, times(1)).findById(nuevaCategoria.getId());
        verify(repository, times(1)).deleteById(nuevaCategoria.getId());
    }
}