package com.cibertec.SistemaWebRestaurante.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cibertec.SistemaWebRestaurante.model.Estado;

public interface EstadoRepository extends JpaRepository<Estado, Long>{
    Optional<Estado> findByNombre(String nombre);

}

