package com.cibertec.auth_service;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.auth_service.entity.Role;
import com.cibertec.auth_service.entity.User;
import com.cibertec.auth_service.repository.RoleRepository;
import com.cibertec.auth_service.repository.UserRepository;

@SpringBootTest
@Transactional // Revierte los cambios en la BD al terminar la prueba para no ensuciarla
class AuthServiceApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testCrearYBuscarUsuarioConRol() {
        // 1. Crear y guardar un Rol de prueba
        Role role = new Role();
        role.setNombre("TEST_ROLE");
        role = roleRepository.save(role);
        assertThat(role.getIdRole()).isNotNull();

        // 2. Crear un Usuario y asignarle el rol
        User user = new User();
        user.setUsername("test_user");
        user.setPassword("123456");
        user.getRoles().add(role);
        
        user = userRepository.save(user);
        assertThat(user.getIdUser()).isNotNull();

        // 3. Buscar el usuario por username (probando tu @Query personalizada)
        Optional<User> usuarioEncontrado = userRepository.findByUsername("test_user");
        
        assertThat(usuarioEncontrado).isPresent();
        assertThat(usuarioEncontrado.get().getUsername()).isEqualTo("test_user");
        assertThat(usuarioEncontrado.get().getRoles()).hasSize(1);
        assertThat(usuarioEncontrado.get().getRoles().iterator().next().getNombre()).isEqualTo("TEST_ROLE");
        
        System.out.println("✅ ¡Prueba de persistencia de usuario y roles completada con éxito!");
    }
}
