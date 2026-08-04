package com.cibertec.SistemaWebRestaurante.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.SistemaWebRestaurante.model.Categoria;
import com.cibertec.SistemaWebRestaurante.service.CategoriaService;

/**
 * 📌 CONTROLADOR: CategoriaController
 * 
 * Expone endpoints REST para manejar categorías
 */
@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(
	    origins = "http://localhost:4200",
	    allowCredentials = "true"
	)
public class CategoriaController {
	
	@Autowired
	private CategoriaService categoriaService;
	
    /**
     * ✅ Listar todas las categorías
     */
	@GetMapping
    public ResponseEntity<List<Categoria>> listar() {
        return ResponseEntity.ok(categoriaService.listar());
    }
	
    /**
     * ✅ Obtener categoría por ID
     */
	@GetMapping("/{id}")
	public ResponseEntity<Categoria> obtener(@PathVariable Long id) {
	    try {
	        Categoria cat = categoriaService.obtenerPorId(id);
	        return ResponseEntity.ok(cat);
	    } catch (RuntimeException e) {
	        return ResponseEntity.notFound().build();
	    }
	}
	
    /**
     * ✅ Crear nueva categoría
     * 
     * 🔥 RECOMENDACIÓN: usar ruta base POST (no /nuevo)
     */
	@PostMapping("/nuevo")
    public ResponseEntity<Categoria> crear(@RequestBody Categoria categoria) {
        Categoria nueva = categoriaService.guardar(categoria);
        return ResponseEntity.status(201).body(nueva);
    }
	
    /**
     * ✅ Actualizar categoría
     */
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizar(@PathVariable Long id, @RequestBody Categoria categoria) {
        categoria.setIdCat(id);
        return ResponseEntity.ok(categoriaService.guardar(categoria));
    }
	
    /**
     * ✅ Eliminar categoría
     */
	@DeleteMapping("/{id}")
	public ResponseEntity<String> eliminar(@PathVariable Long id) {
		categoriaService.eliminar(id);
		return ResponseEntity.ok("Categoria Eliminada Corectamente");
	}
	
	@GetMapping("/buscar")
	public ResponseEntity<List<Categoria>> buscar(@RequestParam String nombre) {
	    return ResponseEntity.ok(categoriaService.buscarPorNombre(nombre));
	}
}
