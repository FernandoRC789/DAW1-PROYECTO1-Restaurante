package com.cibertec.SistemaWebRestaurante.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

//@NoArgsConstructor
//@AllArgsConstructor
public class DetallePedidoDTO {

    private Long comidaId;
    private int cantidad;
	public Long getComidaId() {
		return comidaId;
	}
	public void setComidaId(Long comidaId) {
		this.comidaId = comidaId;
	}
	public int getCantidad() {
		return cantidad;
	}
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	
	// =========================
    // CONSTRUCTOR CON PARAMETROS
	// Y SIN PARAMETROS
    // =========================
	public DetallePedidoDTO(Long comidaId, int cantidad) {
		this.comidaId = comidaId;
		this.cantidad = cantidad;
	}
	public DetallePedidoDTO() {
		super();
	}
	
	

    // getters y setters
    
}