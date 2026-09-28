package com.cibertec.order_service.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cibertec.order_service.model.Pedido;

/**
 * 📦 REPOSITORIO DE PEDIDOS
 * 
 * Esta interfaz gestiona el acceso a la base de datos para la entidad Pedido.
 * Extiende JpaRepository para tener operaciones CRUD automáticas.
 * 
 * 🔹 Incluye:
 * - Búsquedas por estado
 * - Búsquedas por mesa
 * - Filtros dinámicos para el panel administrativo
 * 
 * 📌 NOTA:
 * Spring genera automáticamente las consultas a partir del nombre del método
 * (query derivation), excepto cuando usamos @Query personalizada.
 */

public interface PedidoRepository extends JpaRepository<Pedido, Long>{

    /**
     * 🔎 Buscar pedidos por estado
     * 
     * Ejemplo:
     * - Pendiente
     * - En preparación
     * - Listo
     */
	List<Pedido> findByEstadoid(long estado);
	
    /**
     * 🔎 Buscar pedidos por mesa
     * 
     * Ejemplo:
     * - Mesa 1
     * - Mesa 5
     */
	List<Pedido> findByMesaid(Long mesaid);
	
    /**
     * 🔎 Búsqueda dinámica para ADMIN
     * 
     * Permite filtrar por:
     * - Fecha (opcional)
     * - Número de mesa (opcional)
     * 
     * 🔥 IMPORTANTE:
     * - Si fecha es NULL → no filtra por fecha
     * - Si mesa es NULL → no filtra por mesa
     * 
     * Ejemplo:
     * - Buscar todos los pedidos de hoy
     * - Buscar pedidos de mesa "5"
     * - Buscar pedidos de hoy en mesa "3"
     */
	@Query("SELECT p FROM Pedido p WHERE " +
		       "(:fecha IS NULL OR p.fechaRegistrada = :fecha) AND " +
		       "(:mesa IS NULL OR p.mesaid = :mesa)")
		List<Pedido> buscarPedidosAdmin(
		    @Param("fecha") LocalDate fecha,
		    @Param("mesa") Long mesa
		);
	
	 // =========================
    // 🔥 MEJORAS AGREGADAS
    // =========================

    /**
     * 🆕 Buscar pedidos por mesero
     * (útil para vista del mesero)
     */
	List<Pedido> findByMeseroid(Long idMesero);

    /**
     * 🆕 Buscar pedidos ordenados por fecha descendente
     * (últimos pedidos primero)
     */
    List<Pedido> findAllByOrderByFechaRegistradaDesc();
    
    

}

