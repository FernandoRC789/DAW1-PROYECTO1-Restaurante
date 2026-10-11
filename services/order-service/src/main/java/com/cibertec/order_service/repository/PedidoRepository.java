package com.cibertec.order_service.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cibertec.order_service.model.Pedido;

/**
 * REPOSITORIO DE PEDIDOS
 * 
 * Esta interfaz gestiona el acceso a la base de datos para la entidad Pedido.
 * Extiende JpaRepository para tener operaciones CRUD automáticas.
 */
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /**
     * Buscar pedidos por estado
     */
    List<Pedido> findByEstadoId(long estadoId);

    /**
     * Buscar pedidos por mesa
     */
    List<Pedido> findByMesaId(Long mesaId);

    /**
     * Búsqueda dinámica para ADMIN
     * Permite filtrar por fecha y/o mesa (ambos opcionales)
     */
    @Query("SELECT p FROM Pedido p WHERE " +
           "(:fecha IS NULL OR p.fechaRegistrada = :fecha) AND " +
           "(:mesa IS NULL OR p.mesaId = :mesa)")
    List<Pedido> buscarPedidosAdmin(
        @Param("fecha") LocalDate fecha,
        @Param("mesa") Long mesa
    );

    /**
     * Buscar pedidos por mesero
     */
    List<Pedido> findByMeseroId(Long idMesero);

    /**
     * Buscar pedidos ordenados por fecha descendente
     */
    List<Pedido> findAllByOrderByFechaRegistradaDesc();

    /**
     * Buscar pedidos por cliente
     * Usado por customer-service vía OpenFeign
     */
    List<Pedido> findByClienteId(Long clienteId);

    /**
     * Buscar pedidos que contienen una comida específica
     * Usado por catalog-service vía OpenFeign
     */
    @Query("SELECT DISTINCT p FROM Pedido p JOIN p.detallePedido dp WHERE dp.comida = :comidaId")
    List<Pedido> findByDetallePedidoComida(@Param("comidaId") Long comidaId);
}
