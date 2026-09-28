package com.cibertec.billing_service.dto.external;

import java.math.BigDecimal;

public class DetallePedidoDTO {
    private String nombreProducto; // O nombreComida
    private Integer cantidad;
    private BigDecimal precioUnitario;

    public DetallePedidoDTO() {}

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
}