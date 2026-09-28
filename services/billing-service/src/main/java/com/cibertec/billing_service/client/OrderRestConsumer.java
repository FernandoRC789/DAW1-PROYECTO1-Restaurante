package com.cibertec.billing_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cibertec.billing_service.dto.external.PedidoDTO;

/**
 * 📌 CLIENTE REST: OrderRestConsumer
 * 
 * Se comunica sincrónicamente con 'order-service' vía HTTP.
 */
@FeignClient(name = "order-service", url = "${external.service.order-url:http://localhost:8084}")
public interface OrderRestConsumer {

    @GetMapping("/api/pedidos/{id}")
    PedidoDTO obtenerPedidoPorId(@PathVariable("id") Long id);
}
