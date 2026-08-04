package com.cibertec.SistemaWebRestaurante.controller;

import java.util.List;
import java.util.Map;

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

import com.cibertec.SistemaWebRestaurante.model.Mesa;
import com.cibertec.SistemaWebRestaurante.service.MesaService;
import com.cibertec.SistemaWebRestaurante.utilsEnum.EstadoMesa;

/**
 * 📌 Controlador REST para gestionar las mesas del restaurante
 * 
 * Permite:
 * - Crear, listar, actualizar y eliminar mesas
 * - Cambiar estado de mesas (LIBRE, OCUPADO, etc.)
 * 
 * Base URL: /api/mesas
 */
@RestController
@RequestMapping("/api/mesas")
@CrossOrigin("*") // 🔥 Permite llamadas desde frontend (JS, HTML)
public class MesaController {

	@Autowired
	private MesaService mesaService;
	
    /**
     * ✅ LISTAR TODAS LAS MESAS
     */
	@GetMapping
    public ResponseEntity<List<Mesa>> listar() {
        return ResponseEntity.ok(mesaService.listar());
    }
	
    /**
     * ✅ OBTENER MESA POR ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Mesa> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(mesaService.obtenerPorId(id));
    }
	
    /**
     * ✅ CREAR NUEVA MESA
     * 
     * 🔥 CAMBIO: quitamos "/nuevo" para seguir REST estándar
     */	
    @PostMapping("/nuevo")
     public ResponseEntity<Mesa> crear(@RequestBody Mesa mesa) {
         return ResponseEntity.ok(mesaService.guardar(mesa));
     }
	
     /**
      * ✅ ACTUALIZAR MESA
      */
    @PutMapping("/{id}")
    public ResponseEntity<Mesa> actualizar(@PathVariable Long id, @RequestBody Mesa mesa) {
        mesa.setIdMesa(id);
        return ResponseEntity.ok(mesaService.guardar(mesa));
    }
	
    /**
     * ✅ ELIMINAR MESA
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        mesaService.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Mesa eliminada correctamente"));
    }
	
    /**
     * 🔥 NUEVO: CAMBIAR ESTADO DE MESA
     * 
     * Ejemplo:
     * PATCH /api/mesas/1/estado?estado=OCUPADO
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Mesa> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoMesa estado) {

        return ResponseEntity.ok(mesaService.cambiarEstado(id, estado));
    }
}
