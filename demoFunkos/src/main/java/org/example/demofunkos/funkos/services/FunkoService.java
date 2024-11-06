package org.example.demofunkos.funkos.services;

import org.example.demofunkos.categoria.models.Categoria;
import org.example.demofunkos.funkos.dto.FunkoDto;
import org.example.demofunkos.funkos.models.Funko;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FunkoService {
    List<Funko> getAll();
    Funko getById(String id);
    Funko getByNombre(String nombre);
    Funko save(FunkoDto funkoDto);
    Funko update(String id, FunkoDto funkoDto);
    Funko delete(String id);
}
