package com.cibertec.SistemaWebRestaurante.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cibertec.SistemaWebRestaurante.model.Rol;

/**
 * 📌 REPOSITORIO: RolRepository
 * con sus metodos de:
 * save
 * findbyid
 * delete
 */
public interface RolRepository extends JpaRepository<Rol, Long>{

    /**
     * 🔍 Buscar rol por nombre
     */
    Optional<Rol> findByNombre(String nombre);
}
