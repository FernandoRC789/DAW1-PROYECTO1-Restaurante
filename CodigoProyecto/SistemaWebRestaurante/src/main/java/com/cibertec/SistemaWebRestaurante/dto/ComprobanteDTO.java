package com.cibertec.SistemaWebRestaurante.dto;

import com.cibertec.SistemaWebRestaurante.utilsEnum.MetodoPago;
import com.cibertec.SistemaWebRestaurante.utilsEnum.TipoComprobante;

/**
 * 📌 DTO: ComprobanteDTO
 * 
 * 🔥 Representa los datos que envía el frontend
 * al momento de generar un comprobante de pago.
 * 
 * ❗ IMPORTANTE:
 * No expone entidades completas (Pedido, Cliente, Usuario)
 * solo IDs → seguridad + control
 */
public class ComprobanteDTO {


    /**
     * 🧾 ID del pedido a pagar
     */
    private Long pedidoId;

    /**
     * 👤 ID del cliente (puede ser null para boleta simple)
     */
    private Long clienteId;

    /**
     * 📄 Tipo de comprobante
     * BOLETA o FACTURA
     */
    private TipoComprobante tipoComprobante;

    /**
     * 💳 Método de pago
     * EFECTIVO, YAPE, TARJETA
     */
    private MetodoPago metodoPago;

    
    //GETTERS Y SETTERS
	public Long getPedidoId() {
		return pedidoId;
	}

	public void setPedidoId(Long pedidoId) {
		this.pedidoId = pedidoId;
	}

	public Long getClienteId() {
		return clienteId;
	}

	public void setClienteId(Long clienteId) {
		this.clienteId = clienteId;
	}

	public TipoComprobante getTipoComprobante() {
		return tipoComprobante;
	}

	public void setTipoComprobante(TipoComprobante tipoComprobante) {
		this.tipoComprobante = tipoComprobante;
	}

	public MetodoPago getMetodoPago() {
		return metodoPago;
	}

	public void setMetodoPago(MetodoPago metodoPago) {
		this.metodoPago = metodoPago;
	}

    
}
