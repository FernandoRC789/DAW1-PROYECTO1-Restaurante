package com.cibertec.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cibertec.order_service.dto.ClienteDTO;

/**
 * 📌 CLIENTE FEIGN: ClienteFeignClient
 * 
 * Se comunica sincrónicamente con 'customer-service' vía HTTP.
 * Se usa para validar clientes antes de crear pedidos.
 */
@FeignClient(name = "customer-service")
public interface ClienteFeignClient {

    /**
     * Obtiene un cliente por su ID desde customer-service
     * 
     * @param id ID del cliente
     * @return ClienteDTO con los datos del cliente
     */
    @GetMapping("/api/clientes/{id}")
    ClienteDTO obtenerClientePorId(@PathVariable("id") Long id);
}
