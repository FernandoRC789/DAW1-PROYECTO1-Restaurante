package com.cibertec.SistemaWebRestaurante.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cibertec.SistemaWebRestaurante.dto.UsuarioDTO;
import com.cibertec.SistemaWebRestaurante.model.Rol;
import com.cibertec.SistemaWebRestaurante.model.Usuario;
import com.cibertec.SistemaWebRestaurante.repository.RolRepository;
import com.cibertec.SistemaWebRestaurante.repository.UsuarioRepository;

/**
 * 📌 SERVICE: UsuarioService
 * 
 * 🔥 Maneja lógica de usuarios y roles
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 📋 Listar usuarios
     */
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    /**
     * 🔍 Obtener por ID
     */
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    /**
     * ➕ Crear usuario con roles
     */
    public Usuario crearUsuario(UsuarioDTO dto) {

        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("El username ya existe");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));

        Set<Rol> roles = new HashSet<>();
        
        if (dto.getRoles() == null || dto.getRoles().isEmpty()) {
            throw new RuntimeException("Debe asignar al menos un rol");
        }
        
        for (String nombreRol : dto.getRoles()) {
            Rol rol = rolRepository.findByNombre(nombreRol)
                    .orElseThrow(() -> new RuntimeException("Rol no existe: " + nombreRol));
            roles.add(rol);
        }

        usuario.setRoles(roles);

        return usuarioRepository.save(usuario);
    }

    /**
     * ❌ Eliminar usuario
     */
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }
    
    /**
     * ✏️ Actualizar usuario
     * 
     * Permite:
     * - Cambiar username
     * - Cambiar password (se encripta)
     * - Cambiar roles
     */
    public Usuario actualizarUsuario(Long id, UsuarioDTO dto) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 🔹 username
        if (dto.getUsername() != null) {
            usuario.setUsername(dto.getUsername());
        }

        // 🔹 password
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // 🔹 roles
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {

            Set<Rol> roles = new HashSet<>();

            for (String nombreRol : dto.getRoles()) {
                Rol rol = rolRepository.findByNombre(nombreRol)
                        .orElseThrow(() -> new RuntimeException("Rol no existe: " + nombreRol));
                roles.add(rol);
            }

            usuario.setRoles(roles);
        }

        return usuarioRepository.save(usuario);
    }
    
    public List<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsernameContainingIgnoreCase(username);
    }

    public List<Usuario> buscarPorRol(String rol) {
        return usuarioRepository.findByRolesNombre(rol);
    }

    public Long contarPorRol(String rol) {
        return usuarioRepository.countByRolesNombre(rol);
    }
}