package com.cibertec.billing_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cibertec.billing_service.dto.ClienteDTO;

/**
 * 📌 CLIENTE REST: ClienteRestConsumer
 * 
 * Se comunica de forma sincrónica con 'customer-service' vía HTTP.
 */
// 'name' es el nombre registrado en Eureka / Gateway.
// 'url' se usa mientras se prueba de forma local sin Eureka (ej: puerto 8081).
//@FeignClient(name = "customer-service", url = "${external.service.customer-url:http://localhost:8086}")
@FeignClient(name = "customer-service")
public interface ClienteRestConsumer {

    @GetMapping("/api/clientes/{id}")
    ClienteDTO obtenerClientePorId(@PathVariable("id") Long id);
}
