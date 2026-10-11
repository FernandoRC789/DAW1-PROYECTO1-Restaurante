package com.cibertec.order_service.dto;

import java.math.BigDecimal;

public class DetallePedidoResponseDTO {
    private Long idDetP;
    private Long comidaId;
    private String comidaNombre; // 👈 NUEVO: Nombre del plato
    private int cantidad;
    private BigDecimal precioUni;
    private BigDecimal subtotal;

    // Getters y Setters
    public Long getIdDetP() { return idDetP; }
    public void setIdDetP(Long idDetP) { this.idDetP = idDetP; }
    public Long getComidaId() { return comidaId; }
    public void setComidaId(Long comidaId) { this.comidaId = comidaId; }
    public String getComidaNombre() { return comidaNombre; }
    public void setComidaNombre(String comidaNombre) { this.comidaNombre = comidaNombre;}
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUni() { return precioUni; }
    public void setPrecioUni(BigDecimal precioUni) { this.precioUni = precioUni; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
