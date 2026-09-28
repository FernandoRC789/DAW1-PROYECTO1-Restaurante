package com.cibertec.catalog_service.servic;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cibertec.catalog_service.Repository.CategoriaRepository;
import com.cibertec.catalog_service.model.Categoria;

/**
 * 📌 SERVICIO: CategoriaService
 * 
 * Contiene la lógica de negocio para la gestión de categorías.
 * Intermedia entre Controller y Repository.
 */
@Service
public class CategoriaService {
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
    /**
     * ✅ Listar todas las categorías
     */
	public List<Categoria> listar(){
		return categoriaRepository.findAll();
	}
	
    /**
     * ✅ Obtener categoría por ID
     */
	public Categoria obtenerPorId(Long id) {
		return categoriaRepository.findById(id).orElse(null);
	}
	
    /**
     * ✅ Guardar nueva categoría o actualizar
     * 
     * 🔥 MEJORA: evita duplicados por nombre
     */
	public Categoria guardar(Categoria categoria) {

	    Optional<Categoria> existente = categoriaRepository.findByNombre(categoria.getNombre());

	    // 🔥 VALIDACIÓN CORRECTA (NO bloquear mismo registro)
	    if (existente.isPresent() && !existente.get().getIdCat().equals(categoria.getIdCat())) {
	        throw new RuntimeException("Ya existe una categoría con ese nombre");
	    }

	    return categoriaRepository.save(categoria);
	}
	
    /**
     * ✅ Eliminar categoría
     */
    public void eliminar(Long id) {

        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoría no existe");
        }

        categoriaRepository.deleteById(id);
    }
    
    public List<Categoria> buscarPorNombre(String nombre) {
        return categoriaRepository.findByNombreContainingIgnoreCase(nombre);
    }

}
