package com.cibertec.billing_service.dto.external;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty; // 👈 Importa esto

public class DetallePedidoDTO {
	
    private Long comidaId; // ID de la comida en catalog-service
    
    @JsonProperty("comidaNombre")
    private String comidaNombre;
    
    private Integer cantidad;
    
    @JsonProperty("precioUni")
    private BigDecimal precioUni;

    public DetallePedidoDTO() {}

// --- GETTERS Y SETTERS ---
    
    public Long getComidaId() { return comidaId; }
    @JsonProperty("comidaId")
    public void setComidaId(Long comidaId) { this.comidaId = comidaId; }

    // Permitimos variantes por si acaso
    @JsonProperty("idComida")
    public void setIdComida(Long idComida) {
        if (this.comidaId == null) this.comidaId = idComida;
    }

    public String getComidaNombre() { return comidaNombre; }
    public void setComidaNombre(String comidaNombre) { this.comidaNombre = comidaNombre; }

    @JsonProperty("nombreProducto")
    public void setNombreProducto(String nombreProducto) {
        if (this.comidaNombre == null) this.comidaNombre = nombreProducto;
    }

    public Integer getCantidad() { return cantidad; }
    @JsonProperty("cantidad")
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUni() { return precioUni; }
    public void setPrecioUni(BigDecimal precioUni) { this.precioUni = precioUni; }

    @JsonProperty("precioUnitario")
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        if (this.precioUni == null) this.precioUni = precioUnitario;
    }
    
    // Método puente para compatibilidad con tu ComprobantePagoService si busca getNombreProducto()
    public String getNombreProducto() {
        return this.comidaNombre != null ? this.comidaNombre : "Producto";
    }
    
    // Método puente para compatibilidad con precioUnitario
    public BigDecimal getPrecioUnitario() {
        return this.precioUni != null ? this.precioUni : BigDecimal.ZERO;
    }
}