package com.cibertec.auth_service.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cibertec.auth_service.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{

    /**
     * 🔍 Buscar rol por nombre
     */
    Optional<Role> findByNombre(String nombre);
}
