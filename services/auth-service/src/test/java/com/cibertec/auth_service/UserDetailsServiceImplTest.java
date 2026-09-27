package com.cibertec.auth_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.cibertec.auth_service.entity.Role;
import com.cibertec.auth_service.entity.User;
import com.cibertec.auth_service.repository.UserRepository;
import com.cibertec.auth_service.service.UserDetailsServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void testLoadUserByUsername_Success() {
        // Mock de usuario de base de datos
        User mockUser = new User();
        mockUser.setUsername("admin_nick");
        mockUser.setPassword("$2a$12$encodedPassword");
        
        Role mockRol = new Role();
        mockRol.setNombre("ADMIN");
        mockUser.setRoles(Set.of(mockRol));

        when(userRepository.findByUsername("admin_nick")).thenReturn(Optional.of(mockUser));

        // Ejecutar
        UserDetails userDetails = userDetailsService.loadUserByUsername("admin_nick");

        // Validaciones
        assertNotNull(userDetails);
        assertEquals("admin_nick", userDetails.getUsername());
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("ROLE_ADMIN", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        when(userRepository.findByUsername("no_existe")).thenReturn(Optional.empty());

        // Validar que lanza la excepción esperada
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("no_existe");
        });
    }
}