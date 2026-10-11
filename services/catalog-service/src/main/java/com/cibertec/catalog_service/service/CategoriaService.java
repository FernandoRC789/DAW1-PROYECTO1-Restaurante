package com.cibertec.catalog_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.catalog_service.Repository.CategoriaRepository;
import com.cibertec.catalog_service.model.Categoria;

/**
 * SERVICIO: CategoriaService
 * 
 * Contiene la lógica de negocio para la gestión de categorías.
 * Intermedia entre Controller y Repository.
 */
@Service
public class CategoriaService {
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    /**
     * Listar todas las categorías
     */
    public List<Categoria> listar(){
        return categoriaRepository.findAll();
    }
    
    /**
     * Obtener categoría por ID
     */
    public Categoria obtenerPorId(Long id) {
        return categoriaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));
    }
    
    /**
     * Guardar nueva categoría o actualizar
     * Evita duplicados por nombre
     */
    @Transactional
    public Categoria guardar(Categoria categoria) {
        Optional<Categoria> existente = categoriaRepository.findByNombre(categoria.getNombre());

        if (existente.isPresent() && !existente.get().getIdCat().equals(categoria.getIdCat())) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }

        return categoriaRepository.save(categoria);
    }
    
    /**
     * Eliminar categoría
     */
    @Transactional
    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new IllegalArgumentException("Categoría no existe con ID: " + id);
        }
        categoriaRepository.deleteById(id);
    }
    
    /**
     * Buscar categorías por nombre
     */
    public List<Categoria> buscarPorNombre(String nombre) {
        return categoriaRepository.findByNombreContainingIgnoreCase(nombre);
    }
}
