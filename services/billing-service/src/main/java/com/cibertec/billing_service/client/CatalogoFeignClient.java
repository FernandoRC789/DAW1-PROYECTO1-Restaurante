package com.cibertec.billing_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cibertec.billing_service.dto.ComidaDTO;

/**
 * 📌 CLIENTE FEIGN: CatalogoFeignClient
 * 
 * Se comunica sincrónicamente con 'catalog-service' vía HTTP.
 * Se usa para obtener información de las comidas al generar comprobantes de pago.
 */
@FeignClient(name = "catalog-service")
public interface CatalogoFeignClient {

    /**
     * Obtiene una comida por su ID desde catalog-service
     * 
     * @param id ID de la comida
     * @return ComidaDTO con los datos de la comida
     */
    @GetMapping("/api/comidas/{id}")
    ComidaDTO obtenerComidaPorId(@PathVariable("id") Long id);
}
