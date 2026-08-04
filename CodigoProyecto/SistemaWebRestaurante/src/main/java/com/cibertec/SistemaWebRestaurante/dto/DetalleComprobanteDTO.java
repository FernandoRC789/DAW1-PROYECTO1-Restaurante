package com.cibertec.SistemaWebRestaurante.dto;

import java.math.BigDecimal;

public class DetalleComprobanteDTO {
    private String producto;
    private Integer cantidad;
    private BigDecimal precio;
    
    // getters y setters

	public String getProducto() {
		return producto;
	}
	public void setProducto(String producto) {
		this.producto = producto;
	}
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	public BigDecimal getPrecio() {
		return precio;
	}
	public void setPrecio(BigDecimal precio) {
		this.precio = precio;
	}
	
	//constructor con parametros
	public DetalleComprobanteDTO(String producto, Integer cantidad, BigDecimal precio) {
		super();
		this.producto = producto;
		this.cantidad = cantidad;
		this.precio = precio;
	}
	
	//constructor sin parametros
	public DetalleComprobanteDTO() {
		super();
	}

	
    
}
