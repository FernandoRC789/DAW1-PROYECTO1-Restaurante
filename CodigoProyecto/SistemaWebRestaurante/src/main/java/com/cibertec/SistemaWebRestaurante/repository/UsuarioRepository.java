package com.cibertec.SistemaWebRestaurante.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cibertec.SistemaWebRestaurante.model.Usuario;

/**
 * 📌 REPOSITORIO: UsuarioRepository
 * 
 * Maneja acceso a datos de usuarios
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	
    /**
     * 🔍 Buscar usuario por username (LOGIN)
     */
	@Query("SELECT u FROM Usuario u JOIN FETCH u.roles WHERE u.username = :username")
	Optional<Usuario> findByUsername(@Param("username") String username);
	
    /**
     * ❌ Validar si username ya existe
     */
    boolean existsByUsername(String username);
    
    List<Usuario> findByUsernameContainingIgnoreCase(String username);

    List<Usuario> findByRolesNombre(String nombre);

    Long countByRolesNombre(String nombre);
}
