package org.example.demofunkos.funkos.controllers;

import jakarta.validation.Valid;
import org.example.demofunkos.funkos.dto.FunkoDto;
import org.example.demofunkos.funkos.models.Funko;
import org.example.demofunkos.funkos.services.FunkoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/funkos")
public class FunkoController {
    private FunkoService service;

    @Autowired
    public FunkoController(FunkoService funkoService) {
        this.service = funkoService;
    }

    @GetMapping
    public ResponseEntity<List<Funko>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Funko> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<Funko> save(@Valid @RequestBody FunkoDto funkoDto) {
        var res = service.save(funkoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Funko> update(@PathVariable Long id, @Valid @RequestBody FunkoDto funkoDto) {
        var res = service.update(id, funkoDto);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Funko> delete(@PathVariable Long id) {
        var res = service.delete(id);
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