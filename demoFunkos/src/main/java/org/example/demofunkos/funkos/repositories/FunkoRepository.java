package org.example.demofunkos.funkos.repositories;

import org.example.demofunkos.funkos.models.Funko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FunkoRepository extends JpaRepository<Funko, Long> {
    Optional<Funko> findById(Long id);
}