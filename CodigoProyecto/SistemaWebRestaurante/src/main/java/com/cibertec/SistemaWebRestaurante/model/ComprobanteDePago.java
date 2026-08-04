package com.cibertec.SistemaWebRestaurante.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.cibertec.SistemaWebRestaurante.utilsEnum.MetodoPago;
import com.cibertec.SistemaWebRestaurante.utilsEnum.TipoComprobante;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 📌 ENTIDAD: ComprobanteDePago
 * 
 * Representa el comprobante generado al momento de cobrar un pedido.
 * 
 * 🔥 Usado por:
 * - Cajero (proceso de pago)
 * - Sistema (registro de ventas)
 * 
 * 🔥 Relaciona:
 * - Pedido (1 a 1)
 * - Cliente (muchos comprobantes por cliente)
 * - Usuario (cajero que realiza el cobro)
 * - EstadoComprobante (PAGADO, ANULADO, etc.)
 * 
 * 🔥 IMPORTANTE:
 * Este modelo es clave para control financiero del sistema.
 */
@Entity
//@NoArgsConstructor
//@AllArgsConstructor
@Table(name = "tb_comprobantePago")
public class ComprobanteDePago {
	
    /**
     * 🔑 ID único del comprobante
     */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_comprobantePago")
	private Long idComprobantePago;
	
    /**
     * 🧾 Pedido asociado al comprobante
     * 
     * 🔥 Relación 1 a 1:
     * Un pedido → un comprobante
     */
	@NotNull(message = "El pedido es obligatorio")
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pedido", unique = true)
	@JsonIgnoreProperties({"detallePedido", "mesero"})
	private Pedido pedido;
	
    /**
     * 👤 Cliente que realiza la compra
     * 
     * 🔥 Cascade:
     * Permite guardar cliente automáticamente si no existe
     */
	@ManyToOne
	@JoinColumn(name = "id_cliente", nullable = true)
	private Cliente cliente;
	
    /**
     * 📅 Fecha y hora de emisión del comprobante
     */
	@Column(name = "fecha_emision")
	private LocalDateTime fechaEmision;

    /**
     * 💰 Total del comprobante
     * 
     * 🔥 IMPORTANTE:
     * Se recomienda tomarlo del Pedido (no recalcular)
     */
	@Column(name = "total", nullable = false, precision = 10, scale = 2)
	private BigDecimal total;

    /**
     * 🧾 Tipo de comprobante
     * 
     * Valores:
     * - BOLETA
     * - FACTURA
     */
	@NotNull(message = "El tipo de comprobante es obligatorio")
	@Column(name = "tipo_comprobante")
	@Enumerated(EnumType.STRING)
	private TipoComprobante tipoComprobante;// BOLETA o FACTURA

    /**
     * 🔢 Número del comprobante
     * 
     * Formato tipo SUNAT:
     * - B001-000001
     * - F001-000123
     * 
     * 🔥 Debe ser único
     */
	@Column(name = "numero", unique = true)
	private String numero; // F001-000123

    /**
     * 💳 Método de pago
     * 
     * 🔥 RECOMENDACIÓN:
     * Convertir a ENUM en lugar de String
     */
	@NotNull(message = "El método de pago es obligatorio")
	@Column(name = "metodo_pago")
	@Enumerated(EnumType.STRING)
	private MetodoPago metodoPago;

    /**
     * 📊 Estado del comprobante
     * 
     * Ejemplos:
     * - PAGADO
     * - ANULADO
     */
	@ManyToOne
	@JoinColumn(name = "id_estado_comprobante")
	private EstadoComprobante estado; // PAGADO, ANULADO
	
    /**
     * 👨‍💼 Cajero que realizó el cobro
     */
	@ManyToOne
	@JoinColumn(name = "id_cajero")
	private Usuario cajero;
	
	private LocalDateTime fechaAnulacion;

    /**
     * 🔥 MÉTODO AUTOMÁTICO AL CREAR
     * 
     * - Asigna fecha si no existe
     * - Copia total del pedido
     */
    @PrePersist
    public void prePersist() {

    	if (this.estado == null) {
    	    EstadoComprobante estadoDefault = new EstadoComprobante();
    	    estadoDefault.setIdEstadoComprobante(1L); // ejemplo PAGADO
    	    this.estado = estadoDefault;
    	}
    	
        if (this.fechaEmision == null) {
            this.fechaEmision = LocalDateTime.now();
        }

        // 🔥 MEJORA: asegurar consistencia con pedido
        if (this.total == null && this.pedido != null) {
            this.total = this.pedido.getTotal();
        }
    }
    
    
	// ====== CONSTRUCTOR CON PARAMETROS Y SIN PARAMETROS ======

	public ComprobanteDePago(Long idComprobantePago, @NotNull(message = "El pedido es obligatorio") Pedido pedido,
			Cliente cliente, LocalDateTime fechaEmision, BigDecimal total,
			@NotNull(message = "El tipo de comprobante es obligatorio") TipoComprobante tipoComprobante, String numero,
			@NotNull(message = "El método de pago es obligatorio") MetodoPago metodoPago, EstadoComprobante estado,
			Usuario cajero, LocalDateTime fechaAnulacion) {
		this.idComprobantePago = idComprobantePago;
		this.pedido = pedido;
		this.cliente = cliente;
		this.fechaEmision = fechaEmision;
		this.total = total;
		this.tipoComprobante = tipoComprobante;
		this.numero = numero;
		this.metodoPago = metodoPago;
		this.estado = estado;
		this.cajero = cajero;
		this.fechaAnulacion = fechaAnulacion;
	}

	public ComprobanteDePago() {
	}

	//Getters y Setters

	public Long getIdComprobantePago() {
		return idComprobantePago;
	}


	public void setIdComprobantePago(Long idComprobantePago) {
		this.idComprobantePago = idComprobantePago;
	}


	public Pedido getPedido() {
		return pedido;
	}


	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}


	public Cliente getCliente() {
		return cliente;
	}


	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}


	public LocalDateTime getFechaEmision() {
		return fechaEmision;
	}


	public void setFechaEmision(LocalDateTime fechaEmision) {
		this.fechaEmision = fechaEmision;
	}


	public BigDecimal getTotal() {
		return total;
	}


	public void setTotal(BigDecimal total) {
		this.total = total;
	}


	public TipoComprobante getTipoComprobante() {
		return tipoComprobante;
	}


	public void setTipoComprobante(TipoComprobante tipoComprobante) {
		this.tipoComprobante = tipoComprobante;
	}


	public String getNumero() {
		return numero;
	}


	public void setNumero(String numero) {
		this.numero = numero;
	}


	public MetodoPago getMetodoPago() {
		return metodoPago;
	}


	public void setMetodoPago(MetodoPago metodoPago) {
		this.metodoPago = metodoPago;
	}


	public EstadoComprobante getEstado() {
		return estado;
	}


	public void setEstado(EstadoComprobante estado) {
		this.estado = estado;
	}


	public Usuario getCajero() {
		return cajero;
	}


	public void setCajero(Usuario cajero) {
		this.cajero = cajero;
	}

	public LocalDateTime getFechaAnulacion() {
		return fechaAnulacion;
	}

	public void setFechaAnulacion(LocalDateTime fechaAnulacion) {
		this.fechaAnulacion = fechaAnulacion;
	}
	
	

}
