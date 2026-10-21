package com.cibertec.billing_service.event;

import java.time.LocalDateTime;

/**
 * Evento publicado por billing-service cuando se anula un comprobante de pago.
 */
public class ComprobanteAnuladoEvento {

    private Long idComprobante;
    private Long orderId;
    private String motivo;
    private LocalDateTime fechaAnulacion;

    public ComprobanteAnuladoEvento() {
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFechaAnulacion() {
        return fechaAnulacion;
    }

    public void setFechaAnulacion(LocalDateTime fechaAnulacion) {
        this.fechaAnulacion = fechaAnulacion;
    }
}
