package com.cibertec.SistemaWebRestaurante.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cibertec.SistemaWebRestaurante.model.Mesa;

/**
 * 📦 REPOSITORIO DE MESAS
 * 
 * Maneja acceso a la base de datos para la entidad Mesa.
 * 
 * 🔹 Incluye:
 * - CRUD automático (JpaRepository)
 * - Métodos personalizados para búsqueda
 */
public interface MesaRepository extends JpaRepository<Mesa, Long>{

    /**
     * 🔎 Buscar mesa por número
     * 
     * Ejemplo:
     * - "1"
     * - "10"
     */
    Optional<Mesa> findByNumero(String numero);

    // =========================
    // 🔥 MEJORAS AGREGADAS
    // =========================

    /**
     * 🆕 Verificar si ya existe una mesa con ese número
     * (útil para validaciones antes de guardar)
     */
    boolean existsByNumero(String numero);
}
