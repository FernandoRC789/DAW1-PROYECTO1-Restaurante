package com.cibertec.SistemaWebRestaurante.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cibertec.SistemaWebRestaurante.model.Categoria;

/**
 * 📌 REPOSITORIO: CategoriaRepository
 * 
 * Permite acceder a la base de datos para operaciones CRUD
 * sobre la entidad Categoria.
 * 
 * JpaRepository ya incluye:
 * - save()
 * - findAll()
 * - findById()
 * - deleteById()
 */
public interface CategoriaRepository extends JpaRepository<Categoria, Long>{

    /**
     * 🔍 Buscar categoría por nombre
     * 
     * 🔥 Útil para validaciones o formularios
     */
	Optional<Categoria> findByNombre(String nombre);
    
    // 🔥 MEJORA (OPCIONAL): Validar nombre único
	boolean existsByNombre(String nombre);
	/*
	 * 👉 Usa findByNombre en el service
	 * 👉 Usa existsByNombre solo para validaciones simples 
	 * (ej: frontend rápido)*/

	List<Categoria> findByNombreContainingIgnoreCase(String nombre);
}
