package com.cibertec.order_service.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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

import com.cibertec.order_service.dto.PedidoRequestDTO;
import com.cibertec.order_service.dto.PedidoResponseDTO;
import com.cibertec.order_service.model.Pedido;
import com.cibertec.order_service.repository.PedidoRepository;
import com.cibertec.order_service.service.PedidoService;

import jakarta.validation.Valid;


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
public class PedidoController {

	
    private final PedidoRepository pedidoRepository;
	@Autowired
    private PedidoService pedidoService;

	PedidoController(PedidoRepository pedidoRepository) {
		this.pedidoRepository = pedidoRepository;
	}

    /**
     * 📋 LISTAR TODOS LOS PEDIDOS
     */
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listar() {
        return ResponseEntity.ok(pedidoService.listarConDetalle());
    }

    /**
     * 🔍 OBTENER PEDIDO POR ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> obtenerPorId(@PathVariable Long id) {
    	Pedido pedido = pedidoService.obtenerPorId(id);
        PedidoResponseDTO responseDTO = pedidoService.convertirAResponseDTO(pedido);
        return ResponseEntity.ok(responseDTO);
        }

    /**
     * ➕ CREAR NUEVO PEDIDO
     * 
     * Recibe un DTO desde el frontend.
     * El mesero se asigna automáticamente desde Spring Security.
     */
    @PostMapping
    public ResponseEntity<Pedido> crear(@Valid @RequestBody PedidoRequestDTO dto) {
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

        return ResponseEntity.ok(pedidoService.buscarPorEstado(idEstado));
    }

    /**
     * 🔎 BUSCAR PEDIDOS POR MESA
     */
    @GetMapping("/mesa/{idMesa}")
    public ResponseEntity<List<Pedido>> buscarPorMesa(@PathVariable Long idMesa) {

        

        return ResponseEntity.ok(pedidoService.buscarPorMesa(idMesa));
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
    @PutMapping("/{id}/estado")
    public ResponseEntity<Pedido> cambiarEstado(
            @PathVariable Long id,
            @RequestParam Long estadoId) {

        return ResponseEntity.ok(pedidoService.cambiarEstado(id, estadoId));
    }
    
    /**
     * 🆕 LISTAR PEDIDOS DEL MESERO LOGUEADO (Enriquecido con DTO)
     */
    @GetMapping("/mis-pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> misPedidos() {
        // 🔥 Si en tu servicio tienes un método específico para filtrar por mesero, 
        // asegúrate de mapearlo también a PedidoResponseDTO. 
        // Por ahora, si usas listarConDetalle() te traerá todos los enriquecidos:
        return ResponseEntity.ok(pedidoService.listarConDetalle());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Pedido> actualizar(
            @PathVariable Long id,
            @RequestBody Pedido dto) {

        return ResponseEntity.ok(pedidoService.actualizarPedido(id, dto));
    }
    
    @GetMapping("/listos")
    public List<Pedido> listarListos() {
        return pedidoService.listarPorEstado("LISTO");
    }

    /**
     * Obtiene los pedidos de un cliente específico
     * Usado por customer-service vía OpenFeign
     */
    @GetMapping("/cliente/{clienteId}")
    public List<Pedido> obtenerPedidosPorCliente(@PathVariable Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    /**
     * Obtiene los pedidos que contienen una comida específica
     * Usado por catalog-service vía OpenFeign
     */
    @GetMapping("/comida/{comidaId}")
    public List<Pedido> obtenerPedidosPorComida(@PathVariable Long comidaId) {
        return pedidoRepository.findByDetallePedidoComida(comidaId);
    }

    /**
     * Obtiene los pedidos de un mesero específico
     * Usado por auth-service vía OpenFeign
     */
    @GetMapping("/mesero/{meseroId}")
    public List<Pedido> obtenerPedidosPorMesero(@PathVariable Long meseroId) {
        return pedidoRepository.findByMeseroId(meseroId);
    }

}