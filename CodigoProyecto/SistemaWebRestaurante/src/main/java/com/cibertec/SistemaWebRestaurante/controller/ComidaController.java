package com.cibertec.SistemaWebRestaurante.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.SistemaWebRestaurante.model.Comida;
import com.cibertec.SistemaWebRestaurante.service.ComidaService;

/**
 * 📌 CONTROLADOR: ComidaController
 * 
 * Expone endpoints REST para gestionar comidas.
 * 
 * 🔥 Usado por:
 * - Vista de cocina
 * - Vista de mesero
 * - Vista ADMIN
 */
@RestController
@RequestMapping("/api/comidas")
@CrossOrigin("*")
public class ComidaController {

    @Autowired
    private ComidaService comidaService;

    /**
     * 📋 LISTAR TODAS
     */
    @GetMapping
    public ResponseEntity<List<Comida>> listar() {
        return ResponseEntity.ok(comidaService.listar());
    }

    /**
     * 🔍 OBTENER POR ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Comida> obtener(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(comidaService.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * ➕ CREAR NUEVA COMIDA
     * 
     * 🔥 NOTA:
     * Ya no uses "/nuevo", usa REST estándar
     */
    @PostMapping("/nuevo")
    public ResponseEntity<Comida> crear(@RequestBody Comida comida) {
        Comida nueva = comidaService.guardar(comida);
        return ResponseEntity.status(201).body(nueva);
    }

    /**
     * ✏️ ACTUALIZAR
     */
    @PutMapping("/{id}")
    public ResponseEntity<Comida> actualizar(
            @PathVariable Long id,
            @RequestBody Comida comida) {

        comida.setIdComida(id);

        return ResponseEntity.ok(comidaService.guardar(comida));
    }

    /**
     * ❌ ELIMINAR
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        comidaService.eliminar(id);
        return ResponseEntity.ok("Comida eliminada correctamente");
    }
    
    /**
     * 🔥 CAMBIAR DISPONIBILIDAD
     * 
     * Usado por cocina (activar/desactivar platos)
     */
    @PatchMapping("/{id}/disponible")
    public ResponseEntity<Comida> cambiarDisponible(
            @PathVariable Long id,
            @RequestParam boolean estado) {

        return ResponseEntity.ok(
            comidaService.cambiarDisponible(id, estado)
        );
    }
    
    /**
     * ✅👌 Listar comidas disponibles true
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<Comida>> disponibles() {
        return ResponseEntity.ok(comidaService.listarDisponibles());
    }
    
    @GetMapping("/no-disponibles")
    public ResponseEntity<List<Comida>> noDisponibles() {
        return ResponseEntity.ok(comidaService.listarNoDisponibles());
    }
    
    /**
     * 🔍 BUSCAR POR NOMBRE
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Comida>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(comidaService.buscarPorNombre(nombre));
    }

    /**
     * 🏷️ FILTRAR POR CATEGORIA
     */
    @GetMapping("/categoria/{idCat}")
    public ResponseEntity<List<Comida>> buscarPorCategoria(@PathVariable Long idCat) {
        return ResponseEntity.ok(comidaService.buscarPorCategoria(idCat));
    }
}

