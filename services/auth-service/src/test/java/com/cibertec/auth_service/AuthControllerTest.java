package com.cibertec.auth_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.cibertec.auth_service.config.SecurityConfig;
import com.cibertec.auth_service.controller.AuthController;
import com.cibertec.auth_service.dto.LoginRequestDTO;
import com.cibertec.auth_service.security.JwtUtils;
import com.cibertec.auth_service.service.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean // 👈 Agregamos este mock para satisfacer al JwtRequestFilter
    private UserDetailsServiceImpl userDetailsServiceImpl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testAuthenticateUser_Success() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("admin_nick", "admin123");
        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
        UserDetails userDetails = new User("admin_nick", "password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("mocked-jwt-token-xyz");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token-xyz"))
                .andExpect(jsonPath("$.username").value("admin_nick"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));
    }
    
    @Test
    void testAuthenticateUser_BadCredentials() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("admin_nick", "clave_falsa");

        when(authenticationManager.authenticate(any()))
            .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized()); // 👈 Ahora sí recibirá el 401 gracias al GlobalExceptionHandler
    }
}