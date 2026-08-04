package com.cibertec.SistemaWebRestaurante.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cibertec.SistemaWebRestaurante.model.Categoria;
import com.cibertec.SistemaWebRestaurante.model.Comida;

/**
 * 📌 REPOSITORIO: ComidaRepository
 * 
 * Maneja el acceso a datos de la entidad Comida.
 * 
 * JpaRepository ya incluye:
 * - save()
 * - findAll()
 * - findById()
 * - deleteById()
 */
public interface ComidaRepository extends JpaRepository<Comida, Long>{

    /**
     * ✅ Obtener solo comidas DISPONIBLES
     * 
     * 🔥 Usado por:
     * - Vista del mesero
     * - Crear pedidos
     */
    List<Comida> findByDisponibleTrue();   // solo disponibles

    
    /**
     * ❌ Obtener comidas NO DISPONIBLES (agotadas)
     * 
     * 🔥 Usado por:
     * - Cocina (control de stock)
     */
    List<Comida> findByDisponibleFalse();  // no disponibles
    
    
    /**
     * ✅ Buscar por Categoria
     * 
     */
    List<Comida> findByCategoriaIdCat(Long idCategoria);
    
    /**
     * ✅ Buscar por Nombre
     * 
     */
    List<Comida> findByNombreContainingIgnoreCase(String nombre);
    
    /**
     * ✅ Solo disponibles por Categorias
     * 
     */
    List<Comida> findByCategoriaIdCatAndDisponibleTrue(Long idCategoria);
    
    /**
     * 🏷️ Buscar por categoría
     */
    List<Comida> findByCategoria(Categoria categoria);
}


