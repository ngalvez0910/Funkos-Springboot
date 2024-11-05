package org.example.demofunkos.funkos.mappers;

import org.example.demofunkos.categoria.models.Categoria;
import org.example.demofunkos.categoria.models.TipoCategoria;
import org.example.demofunkos.funkos.dto.FunkoDto;
import org.example.demofunkos.funkos.models.Funko;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class FunkoMapper {
    public Funko toFunko(FunkoDto funkoDto, Categoria categoria) {
        return new Funko(
                null,
                funkoDto.getNombre(),
                funkoDto.getPrecio(),
                categoria,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public Funko toFunkoUpdate(FunkoDto funkoDto, Funko funko, Categoria categoria){
        return new Funko(
                funko.getId(),
                funkoDto.getNombre(),
                funkoDto.getPrecio(),
                categoria,
                funko.getCreatedAt(),
                LocalDateTime.now()
        );
    }
}
