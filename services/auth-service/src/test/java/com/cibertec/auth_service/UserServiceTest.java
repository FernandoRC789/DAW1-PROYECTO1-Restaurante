package com.cibertec.auth_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.cibertec.auth_service.dto.UserCreateDTO;
import com.cibertec.auth_service.dto.UserResponseDTO;
import com.cibertec.auth_service.entity.Role;
import com.cibertec.auth_service.entity.User;
import com.cibertec.auth_service.exception.BadRequestException;
import com.cibertec.auth_service.exception.ResourceNotFoundException;
import com.cibertec.auth_service.repository.RoleRepository;
import com.cibertec.auth_service.repository.UserRepository;
import com.cibertec.auth_service.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void testListarUsuarios_Exito() {
        User user = new User();
        user.setIdUser(1L);
        user.setUsername("nick");
        user.setRoles(Set.of(new Role(1L, "ROLE_ADMIN")));

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponseDTO> resultado = userService.listar();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("nick", resultado.get(0).getUsername());
    }

    @Test
    void testObtenerPorId_Exito() {
        User user = new User();
        user.setIdUser(1L);
        user.setUsername("nick");
        user.setRoles(Set.of(new Role(1L, "ROLE_ADMIN")));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO resultado = userService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("nick", resultado.getUsername());
    }

    @Test
    void testObtenerPorId_NoEncontrado_LanzaExcepcion() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.obtenerPorId(99L);
        });
    }

    @Test
    void testCrearUsuario_Exito() {
        UserCreateDTO dto = new UserCreateDTO("newuser", "123456", List.of("ROLE_ADMIN"));

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hashed_pass");

        Role role = new Role(1L, "ROLE_ADMIN");
        when(roleRepository.findByNombre("ROLE_ADMIN")).thenReturn(Optional.of(role));

        User savedUser = new User();
        savedUser.setIdUser(1L);
        savedUser.setUsername("newuser");
        savedUser.setPassword("hashed_pass");
        savedUser.setRoles(Set.of(role));

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDTO resultado = userService.crearUsuario(dto);

        assertNotNull(resultado);
        assertEquals("newuser", resultado.getUsername());
        assertEquals("ROLE_ADMIN", resultado.getRoles().get(0));
    }

    @Test
    void testCrearUsuario_UsernameDuplicado_LanzaBadRequest() {
        UserCreateDTO dto = new UserCreateDTO("existing", "123456", List.of("ROLE_ADMIN"));
        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            userService.crearUsuario(dto);
        });
    }

    @Test
    void testEliminarUsuario_Exito() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.eliminar(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarUsuario_NoEncontrado_LanzaExcepcion() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.eliminar(99L);
        });
    }
}
