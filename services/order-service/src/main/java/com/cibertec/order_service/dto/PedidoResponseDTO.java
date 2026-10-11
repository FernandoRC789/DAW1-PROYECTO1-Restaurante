package com.cibertec.order_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PedidoResponseDTO {
    private Long idPedido;
    private LocalDate fechaRegistrada;
    private String observaciones;
    private BigDecimal total;
    private Long clienteId;
    
    // Datos enriquecidos legibles
    private Long estadoId;
    private String estadoNombre; // Ej: "PENDIENTE", "EN_COCINA"
    
    private Long mesaId;
    private String mesaNumero;   // Ej: "Mesa 01" o número de mesa real
    
    private Long meseroId;
    private String meseroNombre; // Nombre del mesero
    
    private List<DetallePedidoResponseDTO> detallePedido;

    // Getters y Setters
    public Long getIdPedido() { return idPedido; }
    public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }
    public LocalDate getFechaRegistrada() { return fechaRegistrada; }
    public void setFechaRegistrada(LocalDate fechaRegistrada) { this.fechaRegistrada = fechaRegistrada; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public Long getEstadoId() { return estadoId; }
    public void setEstadoId(Long estadoId) { this.estadoId = estadoId; }
    public String getEstadoNombre() { return estadoNombre; }
    public void setEstadoNombre(String estadoNombre) { this.estadoNombre = estadoNombre; }
    public Long getMesaId() { return mesaId; }
    public void setMesaId(Long mesaId) { this.mesaId = mesaId; }
    public String getMesaNumero() { return mesaNumero; }
    public void setMesaNumero(String mesaNumero) { this.mesaNumero = mesaNumero; }
    public Long getMeseroId() { return meseroId; }
    public void setMeseroId(Long meseroId) { this.meseroId = meseroId; }
    public String getMeseroNombre() { return meseroNombre; }
    public void setMeseroNombre(String meseroNombre) { this.meseroNombre = meseroNombre; }
    public List<DetallePedidoResponseDTO> getDetallePedido() { return detallePedido; }
    public void setDetallePedido(List<DetallePedidoResponseDTO> detallePedido) { this.detallePedido = detallePedido; }
}