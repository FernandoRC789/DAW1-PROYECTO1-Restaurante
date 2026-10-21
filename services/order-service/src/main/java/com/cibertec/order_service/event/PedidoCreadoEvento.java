package com.cibertec.order_service.event;

import java.math.BigDecimal;

/**
 * Evento publicado cuando se crea un nuevo pedido en order-service.
 * Lo consumen operations-service (ocupar mesa), customer-service (historial),
 * catalog-service (estadísticas), etc.
 */
public class PedidoCreadoEvento extends PedidoEvento {

    private Long mesaId;
    private Long meseroId;
    private Long clienteId;
    private BigDecimal total;
    private String estado;

    public PedidoCreadoEvento() {
        super();
        this.tipoEvento = "PEDIDO_CREADO";
    }

    public Long getMesaId() {
        return mesaId;
    }

    public void setMesaId(Long mesaId) {
        this.mesaId = mesaId;
    }

    public Long getMeseroId() {
        return meseroId;
    }

    public void setMeseroId(Long meseroId) {
        this.meseroId = meseroId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
