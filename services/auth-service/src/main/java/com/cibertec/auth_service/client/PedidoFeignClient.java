package com.cibertec.auth_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cibertec.auth_service.dto.PedidoDTO;

/**
 * 📌 CLIENTE FEIGN: PedidoFeignClient
 * 
 * Se comunica sincrónicamente con 'order-service' vía HTTP.
 * Se usa para validar si un mesero tiene pedidos activos antes de eliminarlo.
 */
@FeignClient(name = "order-service")
public interface PedidoFeignClient {

    /**
     * Obtiene los pedidos de un mesero específico desde order-service
     * 
     * @param meseroId ID del mesero
     * @return Lista de pedidos del mesero
     */
    @GetMapping("/api/pedidos/mesero/{meseroId}")
    List<PedidoDTO> obtenerPedidosPorMesero(@PathVariable("meseroId") Long meseroId);
}
