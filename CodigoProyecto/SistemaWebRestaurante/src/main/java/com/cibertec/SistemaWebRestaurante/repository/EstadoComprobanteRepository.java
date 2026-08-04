package com.cibertec.SistemaWebRestaurante.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cibertec.SistemaWebRestaurante.model.EstadoComprobante;

/**
 * 📌 REPOSITORIO: EstadoComprobanteRepository
 * 
 * Maneja los estados del comprobante:
 * PAGADO, ANULADO, etc.
 */
public interface EstadoComprobanteRepository extends JpaRepository<EstadoComprobante, Long>{

	/**
	 * 🔍 Buscar estado por nombre
	 * 
	 * Ejemplo:
	 * "PAGADO"
	 * "ANULADO"
	 */
	Optional<EstadoComprobante> findByNombre(String nombre);

}
