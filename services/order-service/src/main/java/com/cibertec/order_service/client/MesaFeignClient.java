package com.cibertec.order_service.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 📌 CLIENTE FEIGN: MesaFeignClient
 * 
 * Se comunica sincrónicamente con 'operations-service' vía HTTP.
 */
@FeignClient(name = "operations-service")
public interface MesaFeignClient {

    @GetMapping("/api/mesas/{id}")
    Map<String, Object> obtenerMesaPorId(@PathVariable("id") Long id);

 // 🔥 CAMBIO CLAVE: Usamos @RequestParam en lugar de @RequestBody
    @PutMapping("/api/mesas/{id}/estado")
    void actualizarEstadoMesa(
            @PathVariable("id") Long id,
            @RequestParam("estado") String estado);
}
