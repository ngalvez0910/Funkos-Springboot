package org.example.demofunkos.funkos.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.demofunkos.categoria.models.TipoCategoria;

@Data
public class FunkoDto{
        @NotBlank(message = "El nombre no puede estar vacio")
        String nombre;
        @Min(value = 0)
        @Max(value = 50)
        Double precio;
        @NotNull(message = "La categoria no puede estar vacia")
        TipoCategoria categoria;
}