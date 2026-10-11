package com.cibertec.customer_service.dto;

import java.math.BigDecimal;

/**
 * 📌 DTO: PedidoDTO
 * 
 * Objeto de transferencia de datos para comunicar customer-service con order-service.
 * Se usa para consultar el historial de pedidos de un cliente.
 */
public class PedidoDTO {
    private Long idPedido;
    private Long mesaId;
    private Long meseroId;
    private Long estadoid;
    private String estado;
    private BigDecimal total;

    // Constructores
    public PedidoDTO() {}

    public PedidoDTO(Long idPedido, Long mesaId, Long meseroId, Long estadoid, String estado, BigDecimal total) {
        this.idPedido = idPedido;
        this.mesaId = mesaId;
        this.meseroId = meseroId;
        this.estadoid = estadoid;
        this.estado = estado;
        this.total = total;
    }

    // Getters y Setters
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

    public Long getEstadoid() {
        return estadoid;
    }

    public void setEstadoid(Long estadoid) {
        this.estadoid = estadoid;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
