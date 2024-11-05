package org.example.demofunkos.categoria.mappers;

import org.example.demofunkos.categoria.dto.CategoriaDto;
import org.example.demofunkos.categoria.models.Categoria;
import org.example.demofunkos.categoria.models.TipoCategoria;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CategoriaMapper {
    public Categoria fromDto(CategoriaDto categoriaDto) {
        var categoria = new Categoria();
        categoria.setNombre(TipoCategoria.valueOf(categoriaDto.getNombre()));
        return categoria;
    }

    public Categoria toCategoria(CategoriaDto categoriaDto, Categoria categoria){
        return new Categoria(
                categoria.getId(),
                categoriaDto.getNombre() != null ? TipoCategoria.valueOf(categoriaDto.getNombre()) : categoria.getNombre(),
                categoria.getCreatedAt(),
                LocalDateTime.now(),
                categoriaDto.getActivado() != null ? categoriaDto.getActivado() : categoria.getActivado()
        );
    }
}
