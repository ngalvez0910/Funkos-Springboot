package org.example.demofunkos.categoria.services;

import org.example.demofunkos.categoria.dto.CategoriaDto;
import org.example.demofunkos.categoria.models.Categoria;
import org.example.demofunkos.categoria.models.TipoCategoria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface CategoriaService {
    List<Categoria> getAll();
    Categoria getById(UUID id);
    Categoria getByNombre(TipoCategoria nombre);
    Categoria save(CategoriaDto categoriaDto);
    Categoria update(UUID id, CategoriaDto categoriaDto);
    Categoria delete(UUID id, CategoriaDto categoriaDto);
}