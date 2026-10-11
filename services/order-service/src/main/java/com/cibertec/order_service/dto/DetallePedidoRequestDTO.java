package com.cibertec.order_service.dto;

public class DetallePedidoRequestDTO {
    private Long comidaId; // Mapea el id que manda Angular
    private int cantidad;

    public Long getComidaId() { return comidaId; }
    public void setComidaId(Long comidaId) { this.comidaId = comidaId; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
