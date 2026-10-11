package com.cibertec.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 📌 CLIENTE FEIGN: UsuarioFeignClient
 * 
 * Se comunica sincrónicamente con 'auth-service' vía HTTP.
 */
@FeignClient(name = "auth-service")
public interface UsuarioFeignClient {

    @GetMapping("/api/usuarios/{id}")
    Map<String, Object> obtenerUsuarioPorId(@PathVariable("id") Long id);
}
