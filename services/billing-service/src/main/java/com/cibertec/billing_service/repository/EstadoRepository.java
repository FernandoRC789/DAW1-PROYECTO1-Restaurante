package com.cibertec.billing_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cibertec.billing_service.model.Estado;

public interface EstadoRepository extends JpaRepository<Estado, Long>{
    Optional<Estado> findByNombre(String nombre);

}

