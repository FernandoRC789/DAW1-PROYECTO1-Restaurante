package com.cibertec.billing_service.dto;

import java.math.BigDecimal;

/**
 * 📌 DTO: ComidaDTO
 * 
 * Objeto de transferencia de datos para comunicar billing-service con catalog-service.
 * Se usa para obtener información de las comidas al generar comprobantes de pago.
 */
public class ComidaDTO {
    private Long idComida;
    private String nombre;
    private String descripcion;
    private BigDecimal precioUni;
    private boolean disponible;

    // Constructores
    public ComidaDTO() {}

    public ComidaDTO(Long idComida, String nombre, String descripcion, BigDecimal precioUni, boolean disponible) {
        this.idComida = idComida;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioUni = precioUni;
        this.disponible = disponible;
    }

    // Getters y Setters
    public Long getIdComida() {
        return idComida;
    }

    public void setIdComida(Long idComida) {
        this.idComida = idComida;
    }

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

    public BigDecimal getPrecioUni() {
        return precioUni;
    }

    public void setPrecioUni(BigDecimal precioUni) {
        this.precioUni = precioUni;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
