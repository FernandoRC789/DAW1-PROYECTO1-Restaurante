package com.cibertec.billing_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cibertec.billing_service.model.EstadoComprobante;

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