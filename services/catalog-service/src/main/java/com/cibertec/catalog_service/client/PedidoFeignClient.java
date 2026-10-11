package com.cibertec.catalog_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cibertec.catalog_service.dto.PedidoDTO;

/**
 * 📌 CLIENTE FEIGN: PedidoFeignClient
 * 
 * Se comunica sincrónicamente con 'order-service' vía HTTP.
 * Se usa para consultar estadísticas de ventas por comida.
 */
@FeignClient(name = "order-service")
public interface PedidoFeignClient {

    /**
     * Obtiene los pedidos que contienen una comida específica desde order-service
     * 
     * @param comidaId ID de la comida
     * @return Lista de pedidos que contienen la comida
     */
    @GetMapping("/api/pedidos/comida/{comidaId}")
    List<PedidoDTO> obtenerPedidosPorComida(@PathVariable("comidaId") Long comidaId);
}
