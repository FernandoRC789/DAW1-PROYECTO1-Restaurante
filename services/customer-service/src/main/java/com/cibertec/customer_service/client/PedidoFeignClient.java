package com.cibertec.customer_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cibertec.customer_service.dto.PedidoDTO;

/**
 * 📌 CLIENTE FEIGN: PedidoFeignClient
 * 
 * Se comunica sincrónicamente con 'order-service' vía HTTP.
 * Se usa para consultar el historial de pedidos de un cliente.
 */
@FeignClient(name = "order-service")
public interface PedidoFeignClient {

    /**
     * Obtiene el historial de pedidos de un cliente desde order-service
     * 
     * @param clienteId ID del cliente
     * @return Lista de pedidos del cliente
     */
    @GetMapping("/api/pedidos/cliente/{clienteId}")
    List<PedidoDTO> obtenerPedidosPorCliente(@PathVariable("clienteId") Long clienteId);
}
