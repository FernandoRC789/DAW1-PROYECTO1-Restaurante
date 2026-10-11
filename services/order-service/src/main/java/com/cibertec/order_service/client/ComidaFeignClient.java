package com.cibertec.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 📌 CLIENTE FEIGN: ComidaFeignClient
 * 
 * Se comunica sincrónicamente con 'catalog-service' vía HTTP.
 */
@FeignClient(name = "catalog-service")
public interface ComidaFeignClient {

    @GetMapping("/api/comidas/{id}")
    Map<String, Object> obtenerComidaPorId(@PathVariable("id") Long id);

    @GetMapping("/api/comidas/{id}/precio")
    BigDecimal obtenerPrecioComida(@PathVariable("id") Long id);

    @GetMapping("/api/comidas/{id}/disponible")
    boolean estaDisponible(@PathVariable("id") Long id);
}
