package com.cibertec.auth_service;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // o la ruta que te sugiera Eclipse
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.auth_service.config.SecurityConfig;
import com.cibertec.auth_service.exception.BadRequestException;
import com.cibertec.auth_service.exception.GlobalExceptionHandler;
import com.cibertec.auth_service.exception.ResourceNotFoundException;
import com.cibertec.auth_service.security.JwtUtils;
import com.cibertec.auth_service.service.UserDetailsServiceImpl;
import com.cibertec.auth_service.service.UserService;

// Controlador temporal exclusivo para probar que las excepciones devuelven los códigos correctos
@RestController
class TestController {
    @GetMapping("/test-not-found")
    public void throwNotFound() {
        throw new ResourceNotFoundException("Recurso no encontrado para pruebas");
    }

    @GetMapping("/test-bad-request")
    public void throwBadRequest() {
        throw new BadRequestException("Petición incorrecta para pruebas");
    }
}

@WebMvcTest({TestController.class, GlobalExceptionHandler.class})
@Import(SecurityConfig.class)
@WithMockUser(username = "admin_nick", roles = {"ADMIN"})
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    // Mockeamos el UserService para que el contexto de seguridad/controladores cargue sin problemas
    @MockitoBean
    private UserService userService;
    
    @MockitoBean
    private JwtUtils jwtUtils; // 👈 Mock para satisfacer al filtro de seguridad

    @MockitoBean
    private UserDetailsServiceImpl userDetailsServiceImpl; // 👈 Mock para satisfacer al filtro de seguridad

    @Test
    void testResourceNotFoundException_DebeRetornar404() throws Exception {
        mockMvc.perform(get("/test-not-found"))
                .andExpect(status().isNotFound()) // Verifica que el código HTTP sea 404
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Recurso no encontrado para pruebas"));
    }

    @Test
    void testBadRequestException_DebeRetornar400() throws Exception {
        mockMvc.perform(get("/test-bad-request"))
                .andExpect(status().isBadRequest()) // Verifica que el código HTTP sea 400
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Petición incorrecta para pruebas"));
    }
}
