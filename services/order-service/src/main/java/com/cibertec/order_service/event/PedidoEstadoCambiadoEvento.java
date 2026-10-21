package com.cibertec.order_service.event;

/**
 * Evento publicado cuando cambia el estado de un pedido (pendiente → cocina → listo → pagado, etc.).
 * Lo consumen operations-service (liberar mesa al pagarse) y billing-service (generar comprobante).
 */
public class PedidoEstadoCambiadoEvento extends PedidoEvento {

    private Long mesaId;
    private Long estadoIdAnterior;
    private Long estadoIdNuevo;

    public PedidoEstadoCambiadoEvento() {
        super();
        this.tipoEvento = "PEDIDO_ESTADO_CAMBIADO";
    }

    public Long getMesaId() {
        return mesaId;
    }

    public void setMesaId(Long mesaId) {
        this.mesaId = mesaId;
    }

    public Long getEstadoIdAnterior() {
        return estadoIdAnterior;
    }

    public void setEstadoIdAnterior(Long estadoIdAnterior) {
        this.estadoIdAnterior = estadoIdAnterior;
    }

    public Long getEstadoIdNuevo() {
        return estadoIdNuevo;
    }

    public void setEstadoIdNuevo(Long estadoIdNuevo) {
        this.estadoIdNuevo = estadoIdNuevo;
    }
}
