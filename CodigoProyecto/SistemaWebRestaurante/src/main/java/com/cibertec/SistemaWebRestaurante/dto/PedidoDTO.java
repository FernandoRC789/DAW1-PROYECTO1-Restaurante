package com.cibertec.SistemaWebRestaurante.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

//@NoArgsConstructor
//@AllArgsConstructor
public class PedidoDTO {

    private Long estadoId;
    private Long mesaId;
    private String observaciones;
    private List<DetallePedidoDTO> detalle;
    
    // getters y setters

	public Long getEstadoId() {
		return estadoId;
	}
	public void setEstadoId(Long estadoId) {
		this.estadoId = estadoId;
	}
	public Long getMesaId() {
		return mesaId;
	}
	public void setMesaId(Long mesaId) {
		this.mesaId = mesaId;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public List<DetallePedidoDTO> getDetalle() {
		return detalle;
	}
	public void setDetalle(List<DetallePedidoDTO> detalle) {
		this.detalle = detalle;
	}
	
	// =========================
    // CONSTRUCTOR CON PARAMETROS
	// Y SIN PARAMETROS
    // =========================
	public PedidoDTO(Long estadoId, Long mesaId, String observaciones, List<DetallePedidoDTO> detalle) {
		this.estadoId = estadoId;
		this.mesaId = mesaId;
		this.observaciones = observaciones;
		this.detalle = detalle;
	}
	public PedidoDTO() {
	}

}
