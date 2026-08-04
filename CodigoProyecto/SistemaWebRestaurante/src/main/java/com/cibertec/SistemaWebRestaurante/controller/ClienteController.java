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

import com.cibertec.SistemaWebRestaurante.model.Cliente;
import com.cibertec.SistemaWebRestaurante.service.ClienteService;

/**
 * 📌 CONTROLADOR: ClienteController
 * 
 * 🔥 RESPONSABILIDAD:
 * Exponer endpoints REST para gestionar clientes
 * 
 * 🔥 USADO POR:
 * - Cajero (crear cliente)
 * - Sistema de comprobantes
 */
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin("*")
public class ClienteController {
	
    @Autowired
    private ClienteService clienteService;

    /**
     * 📋 Listar todos los clientes
     */
    @GetMapping
    public ResponseEntity<List<Cliente>> listar() {
        return ResponseEntity.ok(clienteService.listar());
    }

    /**
     * 🔍 Obtener cliente por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerPorId(id));
    }

    /**
     * 🔍 Buscar cliente por documento (DNI/RUC)
     */
    @GetMapping("/documento/{doc}")
    public ResponseEntity<?> buscar(@PathVariable String doc) {

        Cliente cliente = clienteService.buscarPorDocumento(doc);

        if (cliente == null) {
            return ResponseEntity.noContent().build(); // 🔥 NO 404
        }

        return ResponseEntity.ok(cliente);
    }

    /**
     * 🔍 Buscar clientes por nombre
     */
    @GetMapping("/nombre")
    public ResponseEntity<List<Cliente>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(clienteService.buscarPorNombre(nombre));
    }

    /**
     * ➕ Crear cliente
     */
    @PostMapping("/nuevo")
    public ResponseEntity<Cliente> crear(@RequestBody Cliente cliente) {
        return ResponseEntity.ok(clienteService.guardar(cliente));
    }

    /**
     * ✏️ Actualizar cliente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
        cliente.setIdCliente(id);
        return ResponseEntity.ok(clienteService.guardar(cliente));
    }

    /**
     * ❌ Eliminar cliente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.ok("Cliente eliminado correctamente");
    }

}
