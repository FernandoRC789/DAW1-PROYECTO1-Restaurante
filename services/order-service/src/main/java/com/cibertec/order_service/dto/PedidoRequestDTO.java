package com.cibertec.order_service.dto;

import java.util.List;

public class PedidoRequestDTO {
    private Long estadoId;
    private Long mesaId;
    private Long meseroId;
    private Long clienteId;
    private String observaciones;
    private List<DetallePedidoRequestDTO> detallePedido;

    // Getters y Setters
    public Long getEstadoId() { return estadoId; }
    public void setEstadoId(Long estadoId) { this.estadoId = estadoId; }
    public Long getMesaId() { return mesaId; }
    public void setMesaId(Long mesaId) { this.mesaId = mesaId; }
    public Long getMeseroId() { return meseroId; }
    public void setMeseroId(Long meseroId) { this.meseroId = meseroId; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public List<DetallePedidoRequestDTO> getDetallePedido() { return detallePedido; }
    public void setDetallePedido(List<DetallePedidoRequestDTO> detallePedido) { this.detallePedido = detallePedido; }
}

