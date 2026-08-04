package com.cibertec.SistemaWebRestaurante.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.cibertec.SistemaWebRestaurante.dto.PedidoDTO;
import com.cibertec.SistemaWebRestaurante.model.Estado;
import com.cibertec.SistemaWebRestaurante.model.Mesa;
import com.cibertec.SistemaWebRestaurante.model.Pedido;
import com.cibertec.SistemaWebRestaurante.service.PedidoService;

/**
 * 🎮 CONTROLADOR DE PEDIDOS
 * 
 * Expone los endpoints REST para gestionar pedidos en el sistema.
 * 
 * 🔹 Funcionalidades:
 * - CRUD de pedidos
 * - Filtros por estado, mesa y fecha
 * - Cambio de estado (flujo cocina/caja)
 * 
 * 📌 Este controlador es usado por:
 * - 👨‍🍳 Mesero → crear pedido
 * - 👨‍🍳 Cocina → cambiar estado
 * - 💰 Caja → consultar pedidos
 * - 👨‍💼 Admin → filtrar pedidos
 */
@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin("*") // 🔥 IMPORTANTE para frontend (evita errores CORS)
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    /**
     * 📋 LISTAR TODOS LOS PEDIDOS
     */
    @GetMapping
    public ResponseEntity<List<Pedido>> listar() {
        return ResponseEntity.ok(pedidoService.listar());
    }

    /**
     * 🔍 OBTENER PEDIDO POR ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }

    /**
     * ➕ CREAR NUEVO PEDIDO
     * 
     * Recibe un DTO desde el frontend.
     * El mesero se asigna automáticamente desde Spring Security.
     */
    @PostMapping("/nuevo")
    public ResponseEntity<Pedido> crear(@RequestBody PedidoDTO dto) {
        return ResponseEntity.ok(pedidoService.guardarDesdeDTO(dto));
    }

    /**
     * ❌ ELIMINAR PEDIDO
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        pedidoService.eliminar(id);
        return ResponseEntity.ok("Pedido eliminado correctamente");
    }

    /**
     * 🔎 BUSCAR PEDIDOS POR ESTADO
     */
    @GetMapping("/estado/{idEstado}")
    public ResponseEntity<List<Pedido>> buscarPorEstado(@PathVariable Long idEstado) {

        Estado estado = new Estado();
        estado.setIdEstado(idEstado);

        return ResponseEntity.ok(pedidoService.buscarPorEstado(estado));
    }

    /**
     * 🔎 BUSCAR PEDIDOS POR MESA
     */
    @GetMapping("/mesa/{idMesa}")
    public ResponseEntity<List<Pedido>> buscarPorMesa(@PathVariable Long idMesa) {

        Mesa mesa = new Mesa();
        mesa.setIdMesa(idMesa);

        return ResponseEntity.ok(pedidoService.buscarPorMesa(mesa));
    }

    /**
     * 🔎 FILTRO AVANZADO (ADMIN)
     * 
     * Permite buscar pedidos por:
     * - Fecha (opcional)
     * - Número de mesa (opcional)
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Pedido>> buscarPedidos(
        @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha,

        @RequestParam(required = false) String mesa
    ) {
        return ResponseEntity.ok(pedidoService.buscarPedidos(fecha, mesa));
    }
    
    /**
     * 🔄 CAMBIAR ESTADO DEL PEDIDO
     * 
     * Usado por:
     * - Cocina → "En preparación", "Listo"
     * - Caja → "Pagado"
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Pedido> cambiarEstado(
            @PathVariable Long id,
            @RequestParam Long estadoId) {

        return ResponseEntity.ok(pedidoService.cambiarEstado(id, estadoId));
    }
    
    /**
     * 🆕 LISTAR PEDIDOS DEL MESERO LOGUEADO
     * 
     * Útil para vista del mesero
     */
    @GetMapping("/mis-pedidos")
    public ResponseEntity<List<Pedido>> misPedidos() {
        return ResponseEntity.ok(pedidoService.obtenerPedidosDelMesero());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Pedido> actualizar(
            @PathVariable Long id,
            @RequestBody PedidoDTO dto) {

        return ResponseEntity.ok(pedidoService.actualizarPedido(id, dto));
    }
    
    @GetMapping("/listos")
    public List<Pedido> listarListos() {
        return pedidoService.listarPorEstado("LISTO");
    }
}