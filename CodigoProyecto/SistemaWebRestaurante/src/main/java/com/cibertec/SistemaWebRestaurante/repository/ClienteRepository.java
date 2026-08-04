package com.cibertec.SistemaWebRestaurante.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cibertec.SistemaWebRestaurante.model.Cliente;

/**
 * 📌 REPOSITORIO: ClienteRepository
 * 
 * 🔥 RESPONSABILIDAD:
 * Acceso a base de datos para la entidad Cliente
 * 
 * JpaRepository ya proporciona:
 * - save()
 * - findAll()
 * - findById()
 * - deleteById()
 * - existsById()
 * 
 * 🔥 Aquí agregamos métodos personalizados útiles para negocio
 */
public interface ClienteRepository extends JpaRepository<Cliente, Long>{
	
    /**
     * 🔍 Buscar cliente por documento (DNI o RUC)
     * 
     * 🔥 MUY IMPORTANTE:
     * Se usa al generar comprobante para:
     * - evitar duplicados
     * - reutilizar cliente existente
     */
    Optional<Cliente> findByDocumento(String documento);

    /**
     * 🔍 Verifica si ya existe un cliente con ese documento
     * 
     * 🔥 Usado para validaciones antes de registrar
     */
    boolean existsByDocumento(String documento);

    /**
     * 🔍 Buscar clientes por nombre (búsqueda parcial)
     * 
     * 🔥 Ejemplo:
     * "Juan" → devuelve Juan Pérez, Juan Carlos, etc.
     */
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);

}
