package com.cibertec.billing_service.dto.external;

import java.math.BigDecimal;
import java.util.List;

public class PedidoDTO {
    private Long idPedido;
    private BigDecimal total;
    private Long mesaId;
    private String estado;
    private List<DetallePedidoDTO> detalles;

    public PedidoDTO() {}

    public Long getIdPedido() { return idPedido; }
    public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public Long getMesaId() { return mesaId; }
    public void setMesaId(Long mesaId) { this.mesaId = mesaId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<DetallePedidoDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedidoDTO> detalles) { this.detalles = detalles; }
}