package com.cibertec.billing_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import com.cibertec.billing_service.utilsEnum.MetodoPago;
import com.cibertec.billing_service.utilsEnum.TipoComprobante;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

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
		//@OneToOne(fetch = FetchType.EAGER) referencia a mircroservicio order con ID - pendiente implementar conexion
		@JoinColumn(name = "id_pedido", unique = true)
		@JsonIgnoreProperties({"detallePedido", "mesero"})
		private Long orderId;
		
	    /**
	     * 👤 Cliente que realiza la compra
	     * 
	     * 🔥 Cascade:
	     * Permite guardar cliente automáticamente si no existe
	     */
		//@ManyToOne referencia a mircroservicio order con ID - customer implementar conexion
		@JoinColumn(name = "id_cliente", nullable = true)
		private Long cliente;
		
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
		private BigDecimal total = BigDecimal.ZERO;

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
		//@ManyToOne - pendiente user ID
		@JoinColumn(name = "id_cajero")
		private Long userID;
		
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
	            estadoDefault.setIdEstadoComprobante(1L); // Estado por defecto: PAGADO (ID 1)
	            this.estado = estadoDefault;
	        }

	        if (this.fechaEmision == null) {
	            this.fechaEmision = LocalDateTime.now();
	        }
	        
	        // NOTA: El total debe llegar asignado directamente desde la llamada REST/DTO 
	        // al consultar el order-service antes de persistir este comprobante.
	    }
	    
	    
		// ====== CONSTRUCTOR CON PARAMETROS Y SIN PARAMETROS ======

		

		public ComprobanteDePago() {
		}

		public ComprobanteDePago(Long idComprobantePago, @NotNull(message = "El pedido es obligatorio") Long orderId,
				Long cliente, LocalDateTime fechaEmision, BigDecimal total,
				@NotNull(message = "El tipo de comprobante es obligatorio") TipoComprobante tipoComprobante,
				String numero, @NotNull(message = "El método de pago es obligatorio") MetodoPago metodoPago,
				EstadoComprobante estado, Long userID, LocalDateTime fechaAnulacion) {
			super();
			this.idComprobantePago = idComprobantePago;
			this.orderId = orderId;
			this.cliente = cliente;
			this.fechaEmision = fechaEmision;
			this.total = total;
			this.tipoComprobante = tipoComprobante;
			this.numero = numero;
			this.metodoPago = metodoPago;
			this.estado = estado;
			this.userID = userID;
			this.fechaAnulacion = fechaAnulacion;
		}


		//Getters y Setters

		public Long getIdComprobantePago() {
			return idComprobantePago;
		}


		public void setIdComprobantePago(Long idComprobantePago) {
			this.idComprobantePago = idComprobantePago;
		}


		

		

		

		


		public Long getOrderId() {
			return orderId;
		}


		public void setOrderId(Long orderId) {
			this.orderId = orderId;
		}


		public Long getCliente() {
			return cliente;
		}


		public void setCliente(Long cliente) {
			this.cliente = cliente;
		}


		public Long getUserID() {
			return userID;
		}


		public void setUserID(Long userID) {
			this.userID = userID;
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


	


		
		public LocalDateTime getFechaAnulacion() {
			return fechaAnulacion;
		}

		public void setFechaAnulacion(LocalDateTime fechaAnulacion) {
			this.fechaAnulacion = fechaAnulacion;
		}

}
