package org.example.demofunkos.funkos.services;

import org.example.demofunkos.categoria.models.Categoria;
import org.example.demofunkos.funkos.dto.FunkoDto;
import org.example.demofunkos.funkos.models.Funko;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FunkoService {
    List<Funko> getAll();
    Funko getById(Long id);
    Funko save(FunkoDto funkoDto);
    Funko update(Long id, Funko funko);
    Funko delete(Long id);
}
