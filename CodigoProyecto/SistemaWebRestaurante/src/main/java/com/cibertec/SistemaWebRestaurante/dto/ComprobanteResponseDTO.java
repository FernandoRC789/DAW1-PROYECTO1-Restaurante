package com.cibertec.SistemaWebRestaurante.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ComprobanteResponseDTO {
    private String numero;
    private LocalDateTime fechaEmision;
    private String tipoComprobante;
    private String metodoPago;
    private BigDecimal total;

    private String clienteNombre;
    private String clienteDocumento;

    private List<DetalleComprobanteDTO> detalles;

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

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getClienteNombre() {
		return clienteNombre;
	}

	public void setClienteNombre(String clienteNombre) {
		this.clienteNombre = clienteNombre;
	}

	public String getClienteDocumento() {
		return clienteDocumento;
	}

	public void setClienteDocumento(String clienteDocumento) {
		this.clienteDocumento = clienteDocumento;
	}

	public List<DetalleComprobanteDTO> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<DetalleComprobanteDTO> detalles) {
		this.detalles = detalles;
	}

	public ComprobanteResponseDTO(String numero, LocalDateTime fechaEmision, String tipoComprobante, String metodoPago,
			BigDecimal total, String clienteNombre, String clienteDocumento, List<DetalleComprobanteDTO> detalles) {
		this.numero = numero;
		this.fechaEmision = fechaEmision;
		this.tipoComprobante = tipoComprobante;
		this.metodoPago = metodoPago;
		this.total = total;
		this.clienteNombre = clienteNombre;
		this.clienteDocumento = clienteDocumento;
		this.detalles = detalles;
	}

	public ComprobanteResponseDTO() {
	}
    
    
}
