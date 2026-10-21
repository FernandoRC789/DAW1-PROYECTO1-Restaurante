package com.cibertec.operations_service.event;

import java.time.LocalDateTime;

/**
 * Evento recibido de order-service cuando cambia el estado de un pedido.
 *
 * Refleja la estructura del PedidoEstadoCambiadoEvento publicado por
 * order-service para deserializar el JSON por RabbitMQ.
 */
public class PedidoEstadoCambiadoEvento {

    private Long idPedido;
    private String tipoEvento;
    private LocalDateTime fechaEvento;
    private Long mesaId;
    private Long estadoIdAnterior;
    private Long estadoIdNuevo;

    public PedidoEstadoCambiadoEvento() {
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

    public Long getEstadoIdAnterior() {
        return estadoIdAnterior;
    }

    public void setEstadoIdAnterior(Long estadoIdAnterior) {
        this.estadoIdAnterior = estadoIdAnterior;
    }

    public Long getEstadoIdNuevo() {
        return estadoIdNuevo;
    }

    public void setEstadoIdNuevo(Long estadoIdNuevo) {
        this.estadoIdNuevo = estadoIdNuevo;
    }
}
