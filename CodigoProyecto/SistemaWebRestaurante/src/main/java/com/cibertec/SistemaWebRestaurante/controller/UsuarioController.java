package com.cibertec.SistemaWebRestaurante.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.SistemaWebRestaurante.dto.UsuarioDTO;
import com.cibertec.SistemaWebRestaurante.model.Usuario;
import com.cibertec.SistemaWebRestaurante.service.UsuarioService;

/**
 * 📌 CONTROLADOR: UsuarioController
 * 
 * 🔥 Solo ADMIN debería usar esto
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(
	    origins = "http://localhost:4200",
	    allowCredentials = "true"
	)
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * 📋 Listar usuarios
     */
    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    /**
     * 🔍 Obtener por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    /**
     * ➕ Crear usuario
     */
    @PostMapping("/nuevo")
    public ResponseEntity<Usuario> crear(@RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.crearUsuario(dto));
    }

    /**
     * ❌ Eliminar usuario
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok(
            Map.of("message", "Usuario eliminado")
        );
    }
    
    /**
     * ✏️ Actualizar usuario
     */
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(
            @PathVariable Long id,
            @RequestBody UsuarioDTO dto
    ) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, dto));
    }
    
    //🔍 BUSCAR POR USERNAME
    @GetMapping("/buscar")
    public ResponseEntity<List<Usuario>> buscar(@RequestParam String username) {
        return ResponseEntity.ok(usuarioService.buscarPorUsername(username));
    }
    
    //🏷️ FILTRAR POR ROL
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<Usuario>> porRol(@PathVariable String rol) {
        return ResponseEntity.ok(usuarioService.buscarPorRol(rol));
    }
    
    //📊 CONTAR POR ROL
    @GetMapping("/rol/{rol}/count")
    public ResponseEntity<Long> contarPorRol(@PathVariable String rol) {
        return ResponseEntity.ok(usuarioService.contarPorRol(rol));
    }
}
