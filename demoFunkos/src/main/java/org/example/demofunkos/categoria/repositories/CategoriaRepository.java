package org.example.demofunkos.categoria.repositories;

import org.example.demofunkos.categoria.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {
    Optional<Categoria> findByIdAndActivadoTrue(UUID id);
    Optional<Categoria> findByNombre(String nombre);
}