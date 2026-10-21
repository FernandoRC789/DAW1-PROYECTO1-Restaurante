package com.cibertec.customer_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cibertec.customer_service.model.PedidoCliente;

/**
 * Repositorio para el historial local de pedidos por cliente.
 */
@Repository
public interface PedidoClienteRepository extends JpaRepository<PedidoCliente, Long> {

    @Query("SELECT p FROM PedidoCliente p WHERE p.clienteId = :clienteId ORDER BY p.fechaEvento DESC")
    List<PedidoCliente> findByClienteId(@Param("clienteId") Long clienteId);

    @Query("SELECT COUNT(p) FROM PedidoCliente p WHERE p.clienteId = :clienteId")
    Long contarPedidosPorCliente(@Param("clienteId") Long clienteId);
}
