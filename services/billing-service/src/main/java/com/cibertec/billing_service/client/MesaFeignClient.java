package com.cibertec.billing_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 📌 CLIENTE FEIGN: MesaFeignClient
 * 
 * Se comunica sincrónicamente con 'operations-service' vía HTTP.
 * Se usa para liberar la mesa cuando se genera un comprobante de pago.
 */
@FeignClient(name = "operations-service")
public interface MesaFeignClient {

	@PutMapping("/api/mesas/{id}/estado")
    void actualizarEstadoMesa(
            @PathVariable("id") Long id,
            @RequestParam("estado") String estado); // 👈 Usar RequestParam en lugar de RequestBody
}	
