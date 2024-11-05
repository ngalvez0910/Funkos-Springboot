package org.example.demofunkos.categoria.services;

import org.example.demofunkos.categoria.controllers.CategoriaController;
import org.example.demofunkos.categoria.dto.CategoriaDto;
import org.example.demofunkos.categoria.mappers.CategoriaMapper;
import org.example.demofunkos.categoria.models.Categoria;
import org.example.demofunkos.categoria.models.TipoCategoria;
import org.example.demofunkos.categoria.repositories.CategoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = {"categorias"})
public class CategoriaServiceImpl implements CategoriaService {
    private CategoriaRepository repository;
    private CategoriaMapper mapper;

    @Autowired
    public CategoriaServiceImpl(CategoriaRepository repository, CategoriaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<Categoria> getAll() {
        return repository.findAll();
    }

    @Override
    @Cacheable(key = "#id")
    public Categoria getById(UUID id) {
        return repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La categoria con id " + id + " no se ha encontrado.")
        );
    }

    @Override
    @Cacheable(key = "#nombre")
    public Categoria getByNombre(TipoCategoria nombre) {
        return repository.findByNombre(nombre);
    }

    @Override
    @CachePut(key = "#result.id")
    public Categoria save(CategoriaDto categoriaDto) {
        return repository.save(mapper.fromDto(categoriaDto));
    }

    @Override
    @CachePut(key = "#result.id")
    public Categoria update(UUID id, CategoriaDto categoriaDto) {
        System.out.println("Buscando id: " + id);
        var res = repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La categoria con id " + id + " no se ha encontrado.")
        );
        return repository.save(mapper.toCategoria(categoriaDto, res));
    }

    @Override
    @CachePut(key = "#result.id")
    public Categoria delete(UUID id, CategoriaDto categoriaDto) {
        System.out.println("Buscando id: " + id);
        var res = repository.findByIdAndActivadoTrue(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La categoria con id " + id + " no se ha encontrado.")
        );
        return repository.save(mapper.toCategoria(categoriaDto, res));
    }
}