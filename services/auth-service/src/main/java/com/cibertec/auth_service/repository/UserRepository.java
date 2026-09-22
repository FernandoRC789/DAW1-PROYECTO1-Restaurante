package com.cibertec.auth_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cibertec.auth_service.entity.User;

/**
 * 📌 REPOSITORIO: UsuarioRepository
 * 
 * Maneja acceso a datos de usuarios
 */
public interface UserRepository extends JpaRepository<User, Long>{
	
    /**
     * 🔍 Buscar usuario por username (LOGIN)
     */
	@Query("SELECT u FROM Usuario u JOIN FETCH u.roles WHERE u.username = :username")
	Optional<User> findByUsername(@Param("username") String username);
	
    /**
     * ❌ Validar si username ya existe
     */
    boolean existsByUsername(String username);
    
    List<User> findByUsernameContainingIgnoreCase(String username);

    List<User> findByRolesNombre(String nombre);

    Long countByRolesNombre(String nombre);
}
