package com.cibertec.operations_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cibertec.operations_service.dto.PedidoDTO;

/**
 * 📌 CLIENTE FEIGN: PedidoFeignClient
 * 
 * Se comunica sincrónicamente con 'order-service' vía HTTP.
 * Se usa para consultar pedidos activos por mesa.
 */
@FeignClient(name = "order-service")
public interface PedidoFeignClient {

    /**
     * Obtiene los pedidos activos por mesa desde order-service
     * 
     * @param mesaId ID de la mesa
     * @return Lista de pedidos activos de la mesa
     */
    @GetMapping("/api/pedidos/mesa/{mesaId}")
    List<PedidoDTO> obtenerPedidosPorMesa(@PathVariable("mesaId") Long mesaId);
}
