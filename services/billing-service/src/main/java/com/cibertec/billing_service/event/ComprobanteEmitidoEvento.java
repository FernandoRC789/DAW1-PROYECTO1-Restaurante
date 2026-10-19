package com.cibertec.billing_service.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento publicado por billing-service cuando se genera un comprobante de pago.
 * Otros servicios (operations, auditoría, notificaciones, etc.) pueden consumirlo
 * de forma asíncrona sin que billing dependa de ellos.
 */
public class ComprobanteEmitidoEvento {

    private Long idComprobante;
    private Long orderId;
    private Long clienteId;
    private BigDecimal total;
    private String tipoComprobante;
    private String metodoPago;
    private String numero;
    private LocalDateTime fechaEmision;

    public ComprobanteEmitidoEvento() {
    }

    public Long getIdComprobante() {
        return idComprobante;
    }

    public void setIdComprobante(Long idComprobante) {
        this.idComprobante = idComprobante;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
}
