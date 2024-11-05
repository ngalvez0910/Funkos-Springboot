package org.example.demofunkos.funkos.services;

import org.example.demofunkos.categoria.models.TipoCategoria;
import org.example.demofunkos.categoria.services.CategoriaService;
import org.example.demofunkos.funkos.dto.FunkoDto;
import org.example.demofunkos.funkos.mappers.FunkoMapper;
import org.example.demofunkos.funkos.models.Funko;
import org.example.demofunkos.funkos.repositories.FunkoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@CacheConfig(cacheNames = {"funkos"})
public class FunkoServiceImpl implements FunkoService{
    private FunkoRepository repository;
    private FunkoMapper mapper;
    private CategoriaService categoriaService;

    @Autowired
    public FunkoServiceImpl(FunkoRepository repository, FunkoMapper mapper, CategoriaService categoriaService) {
        this.repository = repository;
        this.mapper = mapper;
        this.categoriaService = categoriaService;
    }

    @Override
    public List<Funko> getAll() {
        return repository.findAll();
    }

    @Cacheable
    @Override
    public Funko getById(Long id) {
        return repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El Funko con id " + id + " no se ha encontrado.")
        );
    }

    @CachePut
    @Override
    public Funko save(FunkoDto funkoDto) {
        var categoria = categoriaService.getByNombre(funkoDto.getTipoCategoria());
        return repository.save(mapper.toFunko(funkoDto, categoria));
    }

    @CachePut
    @Override
    public Funko update(Long id, Funko funko) {
        var res = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El Funko con id " + id + " no se ha encontrado.")
        );
        res.setNombre(funko.getNombre());
        res.setPrecio(funko.getPrecio());
        res.setCategoria(funko.getCategoria());
        return repository.save(res);
    }

    @CacheEvict
    @Override
    public Funko delete(Long id) {
        Funko funko = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El Funko con id " + id + " no se ha encontrado.")
        );
        repository.deleteFunkoById(id);
        return funko;
    }
}