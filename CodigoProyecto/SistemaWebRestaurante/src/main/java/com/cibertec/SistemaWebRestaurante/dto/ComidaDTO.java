package com.cibertec.SistemaWebRestaurante.dto;

import java.math.BigDecimal;

public class ComidaDTO {

    private String nombre;
    private String descripcion;
    private Long categoriaId;
    private BigDecimal precioUni;
    private Boolean disponible;
    
    // getters y setters

	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Long getCategoriaId() {
		return categoriaId;
	}
	public void setCategoriaId(Long categoriaId) {
		this.categoriaId = categoriaId;
	}
	public BigDecimal getPrecioUni() {
		return precioUni;
	}
	public void setPrecioUni(BigDecimal precioUni) {
		this.precioUni = precioUni;
	}
	public Boolean getDisponible() {
		return disponible;
	}
	public void setDisponible(Boolean disponible) {
		this.disponible = disponible;
	}
    
}
