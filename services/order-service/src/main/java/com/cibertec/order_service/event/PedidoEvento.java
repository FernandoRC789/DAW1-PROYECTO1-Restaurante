package com.cibertec.order_service.event;

import java.time.LocalDateTime;

/**
 * Clase base para todos los eventos de dominio de pedidos publicados por
 * order-service a través de RabbitMQ.
 */
public abstract class PedidoEvento {

    protected Long idPedido;
    protected String tipoEvento;
    protected LocalDateTime fechaEvento;

    protected PedidoEvento() {
        this.fechaEvento = LocalDateTime.now();
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
}
