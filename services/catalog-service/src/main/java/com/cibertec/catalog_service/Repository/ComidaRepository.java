package com.cibertec.catalog_service.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cibertec.catalog_service.model.Categoria;
import com.cibertec.catalog_service.model.Comida;

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
    List<Comida> findByCategoriaid_IdCat(Long idCategoria);
    
    /**
     * ✅ Buscar por Nombre
     * 
     */
    List<Comida> findByNombreContainingIgnoreCase(String nombre);
    
    /**
     * ✅ Solo disponibles por Categorias
     * 
     */
    List<Comida> findByCategoriaid_IdCatAndDisponibleTrue(Long idCategoria);
    
    /**
     * 🏷️ Buscar por categoría
     */
    List<Comida> findByCategoriaid(Categoria categoria);
}


