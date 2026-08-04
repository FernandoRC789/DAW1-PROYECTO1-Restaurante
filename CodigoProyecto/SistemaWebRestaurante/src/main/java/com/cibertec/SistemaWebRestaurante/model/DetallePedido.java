package com.cibertec.SistemaWebRestaurante.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
//@NoArgsConstructor
//@AllArgsConstructor
@Table(name = "tb_detalle_pedido")
public class DetallePedido {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_detPed")
	private Long idDetP;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pedido")
	@JsonIgnore
	private Pedido pedido;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_comida")
	private Comida comida;
	@NotNull(message = "La cantidad es obligatoria")
	@Min(value = 1)
	@Column(name = "cant_detPed", nullable = false)
	private int cantidad;
	@NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.1")
	@Column(name = "pre_detPed", precision = 10, scale = 2)
	private BigDecimal precioUni;
	@NotNull(message = "El subtotal es obligatorio")
    @DecimalMin(value = "0.1")
	@Column(name = "sub_detPed", precision = 10, scale = 2)
	private BigDecimal subtotal;
	
	public BigDecimal obtenerCalculo() {
	    if (this.precioUni != null && this.cantidad > 0) {
	        return this.precioUni.multiply(BigDecimal.valueOf(this.cantidad));
	    }
	    return BigDecimal.ZERO;
	}
	@PrePersist
	protected void antesDeGuardar() {
	    this.subtotal = obtenerCalculo();
	}
	
	
	
	
	// ====== CONSTRUCTOR CON PARAMETROS Y SIN PARAMETROS ======

	public DetallePedido(Long idDetP, Pedido pedido, Comida comida,
			@NotNull(message = "La cantidad es obligatoria") @Min(1) int cantidad,
			@NotNull(message = "El precio es obligatorio") @DecimalMin("0.1") BigDecimal precioUni,
			@NotNull(message = "El subtotal es obligatorio") @DecimalMin("0.1") BigDecimal subtotal) {
		this.idDetP = idDetP;
		this.pedido = pedido;
		this.comida = comida;
		this.cantidad = cantidad;
		this.precioUni = precioUni;
		this.subtotal = subtotal;
	}
	
	
	public DetallePedido() {
	}
	
	
	public Long getIdDetP() {
		return idDetP;
	}

	public void setIdDetP(Long idDetP) {
		this.idDetP = idDetP;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public Comida getComida() {
		return comida;
	}

	public void setComida(Comida comida) {
		this.comida = comida;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	public BigDecimal getPrecioUni() {
		return precioUni;
	}
	public void setPrecioUni(BigDecimal precioUni) {
		this.precioUni = precioUni;
	}
	public BigDecimal getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}
}

