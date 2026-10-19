package com.cibertec.auth_service.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.auth_service.client.PedidoFeignClient;
import com.cibertec.auth_service.config.RabbitMQConfig;
import com.cibertec.auth_service.dto.PedidoDTO;
import com.cibertec.auth_service.dto.UserCreateDTO;
import com.cibertec.auth_service.dto.UserResponseDTO;
import com.cibertec.auth_service.dto.UserUpdateDTO;
import com.cibertec.auth_service.entity.Role;
import com.cibertec.auth_service.entity.User;
import com.cibertec.auth_service.event.UsuarioActualizadoEvento;
import com.cibertec.auth_service.event.UsuarioCreadoEvento;
import com.cibertec.auth_service.event.UsuarioEliminadoEvento;
import com.cibertec.auth_service.exception.BadRequestException;
import com.cibertec.auth_service.exception.ResourceNotFoundException;
import com.cibertec.auth_service.repository.RoleRepository;
import com.cibertec.auth_service.repository.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

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

    @Autowired
    private PedidoFeignClient pedidoFeignClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    
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
    @Transactional
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

        // 📩 Publicar evento de usuario creado a RabbitMQ
        try {
            UsuarioCreadoEvento evento = new UsuarioCreadoEvento();
            evento.setIdUsuario(savedUser.getIdUser());
            evento.setUsername(savedUser.getUsername());
            evento.setRoles(savedUser.getRoles().stream()
                    .map(Role::getNombre)
                    .toList());
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY_USUARIO_CREADO, evento);
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo publicar evento usuario.creado: " + e.getMessage());
        }

        return mapToResponseDTO(savedUser);
    }

    /**
     * ❌ Eliminar usuario
     * 
     * 🔥 Validación: No se puede eliminar un mesero que tiene pedidos activos
     */
    @Transactional
    public void eliminar(Long id) {
        User usuario = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id + " para eliminar"));
        String usernameABorrar = usuario.getUsername();

        // 🔥 Validar que el mesero no tenga pedidos activos
        try {
            List<PedidoDTO> pedidos = pedidoFeignClient.obtenerPedidosPorMesero(id);
            if (pedidos != null && !pedidos.isEmpty()) {
                throw new BadRequestException(
                    "No se puede eliminar el mesero con ID: " + id + 
                    " porque tiene " + pedidos.size() + " pedido(s) asociado(s)");
            }
        } catch (BadRequestException e) {
            throw e; // Re-lanzar la excepción de validación
        } catch (Exception e) {
            // Si falla la consulta a order-service, permitir la eliminación
            System.err.println("Error al consultar pedidos con order-service: " + e.getMessage());
        }

        userRepository.deleteById(id);

        // 📩 Publicar evento de usuario eliminado a RabbitMQ
        try {
            UsuarioEliminadoEvento evento = new UsuarioEliminadoEvento();
            evento.setIdUsuario(id);
            evento.setUsername(usernameABorrar);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY_USUARIO_ELIMINADO, evento);
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo publicar evento usuario.eliminado: " + e.getMessage());
        }
    }
    
    /**
     * ✏️ Actualizar usuario
     * 
     * Permite:
     * - Cambiar username
     * - Cambiar password (se encripta)
     * - Cambiar roles
     */
    @Transactional
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

        // 📩 Publicar evento de usuario actualizado a RabbitMQ
        try {
            UsuarioActualizadoEvento evento = new UsuarioActualizadoEvento();
            evento.setIdUsuario(updatedUser.getIdUser());
            evento.setUsername(updatedUser.getUsername());
            evento.setRoles(updatedUser.getRoles().stream()
                    .map(Role::getNombre)
                    .toList());
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY_USUARIO_ACTUALIZADO, evento);
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo publicar evento usuario.actualizado: " + e.getMessage());
        }

        return mapToResponseDTO(updatedUser);
    }
    
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
