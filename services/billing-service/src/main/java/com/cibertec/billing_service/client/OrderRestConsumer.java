package com.cibertec.billing_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cibertec.billing_service.dto.external.PedidoDTO;

/**
 * CLIENTE REST: OrderRestConsumer
 * 
 * Se comunica de forma sincrónica con 'order-service' vía HTTP.
 * Se usa para obtener información del pedido al generar comprobantes de pago.
 */
@FeignClient(name = "order-service")
public interface OrderRestConsumer {

    /**
     * Obtiene un pedido por su ID desde order-service
     * 
     * @param id ID del pedido
     * @return PedidoDTO con los datos del pedido
     */
    @GetMapping("/api/pedidos/{id}")
    PedidoDTO obtenerPedidoPorId(@PathVariable("id") Long id);
    
    @PutMapping("/api/pedidos/{id}/estado")
    void cambiarEstadoPedido(@PathVariable("id") Long id, @RequestParam("estadoId") Long estadoId);
    
 // 🔥 Asegúrate de tener este método para cambiar el estado:
    @PutMapping("/api/pedidos/{id}/estado")
    void actualizarEstadoPedido(@PathVariable("id") Long id, @RequestParam("estadoId") Long estadoId);
}