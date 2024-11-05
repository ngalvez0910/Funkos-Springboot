package org.example.demofunkos.categoria.mappers;

import org.example.demofunkos.categoria.dto.CategoriaDto;
import org.example.demofunkos.categoria.models.Categoria;
import org.example.demofunkos.categoria.models.TipoCategoria;
import org.example.demofunkos.funkos.dto.FunkoDto;
import org.example.demofunkos.funkos.models.Funko;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaMapperTest {

    private final CategoriaMapper mapper = new CategoriaMapper();

    @Test
    void fromDto() {
        CategoriaDto categoriaDto = new CategoriaDto();
        categoriaDto.setNombre(TipoCategoria.DISNEY.name());
        categoriaDto.setActivado(true);

        var res = mapper.fromDto(categoriaDto);

        assertAll(
                () -> assertEquals(categoriaDto.getNombre(), res.getNombre().name()),
                () -> assertEquals(categoriaDto.getActivado(), res.getActivado())
        );
    }

    @Test
    void toCategoria() {
        CategoriaDto categoriaDto = new CategoriaDto();
        categoriaDto.setNombre(String.valueOf(TipoCategoria.DISNEY));
        categoriaDto.setActivado(true);

        Categoria categoria = new Categoria(
                null,
                TipoCategoria.DISNEY,
                LocalDateTime.now(),
                LocalDateTime.now(),
                categoriaDto.getActivado()
        );

        var res = mapper.toCategoria(categoriaDto, categoria);

        assertAll(
                () -> assertNull(res.getId()),
                () -> assertEquals(categoriaDto.getId(), res.getId()),
                () -> assertEquals(categoriaDto.getNombre(), res.getNombre().name()),
                () -> assertEquals(categoriaDto.getActivado(), res.getActivado())
        );
    }
}