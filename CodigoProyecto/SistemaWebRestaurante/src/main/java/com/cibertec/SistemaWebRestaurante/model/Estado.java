package com.cibertec.SistemaWebRestaurante.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
//@NoArgsConstructor
//@AllArgsConstructor
@Table(name = "tb_estado")
public class Estado {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_estado")
	private Long idEstado;
	
	@NotBlank(message = "El nombre es obligatorio")
	@Column(name = "nom_estado", nullable = false)
	private String nombre;
	
	@OneToMany(mappedBy = "estado")
	@JsonIgnore
	private List<Pedido> pedidos;

	
	// ====== CONSTRUCTOR CON PARAMETROS Y SIN PARAMETROS ======

	
	public Estado(Long idEstado, @NotBlank(message = "El nombre es obligatorio") String nombre, List<Pedido> pedidos) {
		this.idEstado = idEstado;
		this.nombre = nombre;
		this.pedidos = pedidos;
	}

	public Estado() {
	}


	public Long getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Long idEstado) {
		this.idEstado = idEstado;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Pedido> getPedidos() {
		return pedidos;
	}

	public void setPedidos(List<Pedido> pedidos) {
		this.pedidos = pedidos;
	}
}
