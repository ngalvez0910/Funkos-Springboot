package org.example.demofunkos.categoria.controllers;

import jakarta.validation.Valid;
import org.example.demofunkos.categoria.dto.CategoriaDto;
import org.example.demofunkos.categoria.models.Categoria;
import org.example.demofunkos.categoria.services.CategoriaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {
    private CategoriaServiceImpl service;

    @Autowired
    public CategoriaController(CategoriaServiceImpl service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<Categoria> save(@Valid @RequestBody CategoriaDto categoriaDto) {
        var res = service.save(categoriaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Categoria> update(@PathVariable String id, @RequestBody CategoriaDto categoriaDto) {
        var res = service.update(id, categoriaDto);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> delete(@PathVariable String id, @RequestBody CategoriaDto categoriaDto) {
        var res = service.delete(id, categoriaDto);
        return ResponseEntity.ok(res);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}