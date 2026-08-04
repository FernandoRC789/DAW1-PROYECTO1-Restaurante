package com.cibertec.SistemaWebRestaurante.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_estado_comprobante")
public class EstadoComprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEstadoComprobante;

    @Column(nullable = false)
    private String nombre; // PAGADO, ANULADO, etc.

    //constructor con parametros
	public EstadoComprobante(Long idEstado, String nombre) {
		this.idEstadoComprobante = idEstado;
		this.nombre = nombre;
	}

    //constructor sin parametros
	public EstadoComprobante() {
	}

	
	//Getters y Setters
	public Long getIdEstadoComprobante() {
		return idEstadoComprobante;
	}

	public void setIdEstadoComprobante(Long idEstado) {
		this.idEstadoComprobante = idEstado;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
    
	
}