package org.example.demofunkos.funkos.mappers;

import org.example.demofunkos.categoria.models.Categoria;
import org.example.demofunkos.funkos.dto.FunkoDto;
import org.example.demofunkos.funkos.models.Funko;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FunkoMapperTest {

    private final Categoria categoria = new Categoria(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"), "DISNEY", LocalDateTime.now(), LocalDateTime.now(), true);
    private final FunkoMapper mapper = new FunkoMapper();

    @Test
    void toFunko() {
        FunkoDto funkoDto = new FunkoDto();
        funkoDto.setNombre("Darth Vader");
        funkoDto.setPrecio(10.99);
        funkoDto.setCategoria(categoria.getNombre());

        var res = mapper.toFunko(funkoDto, categoria);

        assertAll(
                () -> assertEquals(funkoDto.getNombre(), res.getNombre()),
                () -> assertEquals(funkoDto.getPrecio(), res.getPrecio()),
                () -> assertEquals(funkoDto.getCategoria(), res.getCategoria().getNombre())
        );
    }

    @Test
    void toFunkoUpdate() {
        FunkoDto funkoDto = new FunkoDto();
        funkoDto.setNombre("Darth Vader");
        funkoDto.setPrecio(10.99);
        funkoDto.setCategoria(categoria.getNombre());

        Funko funko = new Funko(
            null,
            funkoDto.getNombre(),
            funkoDto.getPrecio(),
            categoria,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        var res = mapper.toFunko(funkoDto, categoria);

        assertAll(
                () -> assertNull(res.getId()),
                () -> assertEquals(funkoDto.getNombre(), res.getNombre()),
                () -> assertEquals(funkoDto.getPrecio(), res.getPrecio()),
                () -> assertEquals(funkoDto.getCategoria(), res.getCategoria().getNombre())
        );
    }
}