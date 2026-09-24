package com.cibertec.auth_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // o la ruta que te sugiera Eclipse
import org.springframework.test.web.servlet.MockMvc;

import com.cibertec.auth_service.config.SecurityConfig;
import com.cibertec.auth_service.controller.UserController;
import com.cibertec.auth_service.dto.UserCreateDTO;
import com.cibertec.auth_service.dto.UserResponseDTO;
import com.cibertec.auth_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class) // 👈 Esto le dice que cargue tu configuración con CSRF deshabilitado
@WithMockUser(username = "admin_nick", roles = {"ADMIN"})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // Cambia a @MockBean si tu versión de Spring Boot lo requiere
    private UserService userService;

 // Instanciamos el ObjectMapper directamente para evitar problemas de beans en el test
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testListarUsuariosEndpoint() throws Exception {
        // Usamos "ADMIN" tal cual está en tu script SQL (tb_roles)
        UserResponseDTO user = new UserResponseDTO(1L, "admin_nick", List.of("ADMIN"));
        when(userService.listar()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("admin_nick"))
                .andExpect(jsonPath("$[0].roles[0]").value("ADMIN"));
    }

    @Test
    void testCrearUsuarioEndpoint() throws Exception {
        UserCreateDTO createDTO = new UserCreateDTO("mesero_nick", "mesero123", List.of("MESERO"));
        UserResponseDTO responseDTO = new UserResponseDTO(2L, "mesero_nick", List.of("MESERO"));

        when(userService.crearUsuario(any(UserCreateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("mesero_nick"));
    }
    
}
