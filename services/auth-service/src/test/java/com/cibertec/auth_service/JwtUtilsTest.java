package com.cibertec.auth_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.cibertec.auth_service.security.JwtUtils;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        authentication = Mockito.mock(Authentication.class);

        UserDetails userDetails = new User("admin_nick", "password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    @Test
    void testGenerateAndValidateToken() {
        // 1. Generar token
        String token = jwtUtils.generateJwtToken(authentication);
        assertNotNull(token);

        // 2. Validar token
        boolean isValid = jwtUtils.validateJwtToken(token);
        assertTrue(isValid);

        // 3. Extraer username del token
        String username = jwtUtils.getUserNameFromJwtToken(token);
        assertEquals("admin_nick", username);
    }
}
