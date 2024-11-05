package org.example.demofunkos.categoria.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CategoriaDto {
    UUID id;
    @NotBlank(message = "El nombre no puede estar vacio")
    String nombre;
    Boolean activado;
}
