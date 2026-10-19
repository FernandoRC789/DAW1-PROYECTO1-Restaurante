package com.cibertec.catalog_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad de proyección local para estadísticas de ventas por pedido.
 *
 * catalog-service no consulta a order-service por HTTP; en su lugar
 * consume el evento RabbitMQ y guarda aquí una copia ligera de cada pedido
 * para poder generar reportes de ventas localmente.
 */
@Entity
@Table(name = "pedido_estadistica")
public class PedidoEstadistica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idPedido;
    private Long mesaId;
    private Long meseroId;
    private Long clienteId;
    private BigDecimal total;
    private String estado;
    private LocalDateTime fechaEvento;

    public PedidoEstadistica() {
    }

    public PedidoEstadistica(Long idPedido, Long mesaId, Long meseroId, Long clienteId,
                             BigDecimal total, String estado, LocalDateTime fechaEvento) {
        this.idPedido = idPedido;
        this.mesaId = mesaId;
        this.meseroId = meseroId;
        this.clienteId = clienteId;
        this.total = total;
        this.estado = estado;
        this.fechaEvento = fechaEvento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public Long getMesaId() {
        return mesaId;
    }

    public void setMesaId(Long mesaId) {
        this.mesaId = mesaId;
    }

    public Long getMeseroId() {
        return meseroId;
    }

    public void setMeseroId(Long meseroId) {
        this.meseroId = meseroId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(LocalDateTime fechaEvento) {
        this.fechaEvento = fechaEvento;
    }
}
