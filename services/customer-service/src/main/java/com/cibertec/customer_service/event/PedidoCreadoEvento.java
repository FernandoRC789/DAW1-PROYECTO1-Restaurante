package com.cibertec.customer_service.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento recibido de order-service cuando se crea un pedido.
 *
 * Refleja la estructura del PedidoCreadoEvento publicado por order-service
 * para deserializar el JSON que llega por RabbitMQ.
 */
public class PedidoCreadoEvento {

    private Long idPedido;
    private String tipoEvento;
    private LocalDateTime fechaEvento;
    private Long mesaId;
    private Long meseroId;
    private Long clienteId;
    private BigDecimal total;
    private String estado;

    public PedidoCreadoEvento() {
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(LocalDateTime fechaEvento) {
        this.fechaEvento = fechaEvento;
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
}
