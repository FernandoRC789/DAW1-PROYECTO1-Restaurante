package com.cibertec.catalog_service.Repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cibertec.catalog_service.model.PedidoEstadistica;

/**
 * Repositorio para la proyección local de estadísticas de pedidos.
 */
@Repository
public interface PedidoEstadisticaRepository extends JpaRepository<PedidoEstadistica, Long> {

    @Query("SELECT COUNT(p) FROM PedidoEstadistica p")
    Long contarTotalPedidos();

    @Query("SELECT COALESCE(SUM(p.total), 0) FROM PedidoEstadistica p")
    BigDecimal sumarTotalVentas();

    @Query("SELECT p FROM PedidoEstadistica p WHERE p.clienteId = :clienteId ORDER BY p.fechaEvento DESC")
    List<PedidoEstadistica> findByClienteId(@Param("clienteId") Long clienteId);
}
