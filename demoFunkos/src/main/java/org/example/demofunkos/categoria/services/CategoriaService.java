package org.example.demofunkos.categoria.services;

import org.example.demofunkos.categoria.dto.CategoriaDto;
import org.example.demofunkos.categoria.models.Categoria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface CategoriaService {
    List<Categoria> getAll();
    Categoria getById(String id);
    Categoria getByNombre(String nombre);
    Categoria save(CategoriaDto categoriaDto);
    Categoria update(String id, CategoriaDto categoriaDto);
    Categoria delete(String id, CategoriaDto categoriaDto);
}