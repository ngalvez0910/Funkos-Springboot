package org.example.demofunkos.categoria.mappers;

import org.example.demofunkos.categoria.dto.CategoriaDto;
import org.example.demofunkos.categoria.models.Categoria;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaMapperTest {

    private final CategoriaMapper mapper = new CategoriaMapper();

    @Test
    void fromDto() {
        CategoriaDto categoriaDto = new CategoriaDto();
        categoriaDto.setNombre("DISNEY");
        categoriaDto.setActivado(true);

        var res = mapper.fromDto(categoriaDto);

        assertAll(
                () -> assertEquals(categoriaDto.getNombre(), res.getNombre()),
                () -> assertEquals(categoriaDto.getActivado(), res.getActivado())
        );
    }

    @Test
    void toCategoria() {
        CategoriaDto categoriaDto = new CategoriaDto();
        categoriaDto.setNombre("DISNEY");
        categoriaDto.setActivado(true);

        Categoria categoria = new Categoria(
                null,
                "DISNEY",
                LocalDateTime.now(),
                LocalDateTime.now(),
                categoriaDto.getActivado()
        );

        var res = mapper.toCategoria(categoriaDto, categoria);

        assertAll(
                () -> assertNull(res.getId()),
                () -> assertEquals(categoriaDto.getId(), res.getId()),
                () -> assertEquals(categoriaDto.getNombre(), res.getNombre()),
                () -> assertEquals(categoriaDto.getActivado(), res.getActivado())
        );
    }
}