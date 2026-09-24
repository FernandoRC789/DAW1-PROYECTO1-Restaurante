package com.cibertec.auth_service.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cibertec.auth_service.dto.UserCreateDTO;
import com.cibertec.auth_service.dto.UserResponseDTO;
import com.cibertec.auth_service.dto.UserUpdateDTO;
import com.cibertec.auth_service.entity.Role;
import com.cibertec.auth_service.entity.User;
import com.cibertec.auth_service.exception.BadRequestException;
import com.cibertec.auth_service.exception.ResourceNotFoundException;
import com.cibertec.auth_service.repository.RoleRepository;
import com.cibertec.auth_service.repository.UserRepository;

/**
 * 📌 SERVICE: UserService
 * 
 * 🔥 Maneja lógica de usuarios y roles
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 🔄 Método privado para mapear de Entidad User a UserResponseDTO (Clean Code)
     */
    private UserResponseDTO mapToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setIdUser(user.getIdUser());
        dto.setUsername(user.getUsername());
        
        // Convertimos el Set de roles de la entidad a una List<String> de nombres
        List<String> nombresRoles = user.getRoles().stream()
                .map(Role::getNombre)
                .collect(Collectors.toList());
        
        dto.setRoles(nombresRoles);
        return dto;
    }

    /**
     * 📋 Listar usuarios
     */
    public List<UserResponseDTO> listar() {
        return userRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 🔍 Obtener por ID
     */
    public UserResponseDTO obtenerPorId(Long id) {
    	User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    	return mapToResponseDTO(user);
    }

    /**
     * ➕ Crear usuario con roles
     */
    public UserResponseDTO crearUsuario(UserCreateDTO dto) {

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BadRequestException("El nombre de usuario '" + dto.getUsername() + "' ya se encuentra registrado");
        }

        User usuario = new User();
        usuario.setUsername(dto.getUsername());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        
        if (dto.getRoles() == null || dto.getRoles().isEmpty()) {
            throw new BadRequestException("Debe asignar al menos un rol");
        }
        
        Set<Role> roles = new HashSet<>();
        for (String nombreRol : dto.getRoles()) {
            Role rol = roleRepository.findByNombre(nombreRol)
                    .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + nombreRol));
            roles.add(rol);
        }

        usuario.setRoles(roles);
        User savedUser = userRepository.save(usuario);
        
        return mapToResponseDTO(savedUser);
    }

    /**
     * ❌ Eliminar usuario
     */
    public void eliminar(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + id + " para eliminar");
        }
        userRepository.deleteById(id);
    }
    
    /**
     * ✏️ Actualizar usuario
     * 
     * Permite:
     * - Cambiar username
     * - Cambiar password (se encripta)
     * - Cambiar roles
     */
    public UserResponseDTO actualizarUsuario(Long id, UserUpdateDTO dto) {

        User usuario = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        // 🔹 username
        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            usuario.setUsername(dto.getUsername());
        }

        // 🔹 password
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // 🔹 roles
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {

            Set<Role> roles = new HashSet<>();

            for (String nombreRol : dto.getRoles()) {
                Role rol = roleRepository.findByNombre(nombreRol)
                        .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + nombreRol));
                roles.add(rol);
            }

            usuario.setRoles(roles);
        }

        User updatedUser = userRepository.save(usuario);
        return mapToResponseDTO(updatedUser);    }
    
    public List<UserResponseDTO> buscarPorUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<UserResponseDTO> buscarPorRol(String rol) {
        return userRepository.findByRolesNombre(rol).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public Long contarPorRol(String rol) {
        return userRepository.countByRolesNombre(rol);
    }
}
