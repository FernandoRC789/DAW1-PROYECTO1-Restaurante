package com.cibertec.SistemaWebRestaurante.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
//@NoArgsConstructor
//@AllArgsConstructor
@Table(name = "tb_pedido")
public class Pedido {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_ped")
	private Long idPedido;
	
    /**
     * 🧾 RELACIÓN: Pedido -> Detalles
     * 
     * Un pedido tiene muchos detalles.
     * Cascade ALL permite guardar automáticamente los detalles.
     */
	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
	private List<DetallePedido> detallePedido;
	
    /**
     * 🔄 Estado del pedido
     * Ejemplo: Pendiente, En cocina, Listo, Pagado
     */
	@NotNull(message = "Selecciona un estado")
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_estado")
	private Estado estado;
	
	 /**
     * 🍽️ Mesa asociada al pedido
     */
	@NotNull(message = "Selecciona una mesa")
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_mesa")
	private Mesa mesa;
	
    /**
     * 📅 Fecha del pedido
     */
	@NotNull(message = "Ingrese la fecha")
	@PastOrPresent
	@JsonFormat(pattern = "yyyy-MM-dd") 
	@Column(name = "fecha_ped", nullable = false)
	private LocalDate fechaRegistrada;
	
    /**
     * 📝 Observaciones opcionales
     */
	@Column(name = "obs_ped", nullable = true)
	private String observaciones;
	
    /**
     * 💰 Total del pedido
     * Se calcula automáticamente antes de persistir
     */
	@NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "total_ped", precision = 10, scale = 2)
	private BigDecimal total = BigDecimal.ZERO;
	
    /**
     * 👨‍🍳 Mesero que registró el pedido
     */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_mesero")
	private Usuario mesero;
	
    // =========================
    // 🔥 LÓGICA AUTOMÁTICA
    // =========================

    /**
     * ⚙️ Método que se ejecuta ANTES de guardar en BD
     * 
     * Funciones:
     * - Asignar fecha automática si no viene
     * - Calcular total del pedido sumando detalles
     */
	@JsonIgnore
    @PrePersist
    protected void onCreate() {
		
        // 🗓️ Fecha automática
        if (this.fechaRegistrada == null) {
            this.fechaRegistrada = LocalDate.now();
        }
        
        // 💰 Cálculo del total
        if (this.detallePedido != null && !this.detallePedido.isEmpty()) {
            BigDecimal suma = BigDecimal.ZERO;
            for (DetallePedido det : detallePedido) {
                suma = suma.add(det.obtenerCalculo());
            }
            this.total = suma;
        } else if (this.total == null) {
            this.total = BigDecimal.ZERO;
        }
    }

	// =========================
    // CONSTRUCTOR CON PARAMETROS
	// Y SIN PARAMETROS
    // =========================
	
    public Pedido(Long idPedido, List<DetallePedido> detallePedido,
			@NotNull(message = "Selecciona un estado") Estado estado,
			@NotNull(message = "Selecciona una mesa") Mesa mesa,
			@NotNull(message = "Ingrese la fecha") @PastOrPresent LocalDate fechaRegistrada, String observaciones,
			@NotNull @DecimalMin("0.0") BigDecimal total, Usuario mesero) {
		this.idPedido = idPedido;
		this.detallePedido = detallePedido;
		this.estado = estado;
		this.mesa = mesa;
		this.fechaRegistrada = fechaRegistrada;
		this.observaciones = observaciones;
		this.total = total;
		this.mesero = mesero;
	}



	public Pedido() {
	}

	// =========================
    // GETTERS / SETTERS
    // =========================
	public Long getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(Long idPedido) {
		this.idPedido = idPedido;
	}

	public List<DetallePedido> getDetallePedido() {
		return detallePedido;
	}

	public void setDetallePedido(List<DetallePedido> detallePedido) {
		this.detallePedido = detallePedido;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Mesa getMesa() {
		return mesa;
	}

	public void setMesa(Mesa mesa) {
		this.mesa = mesa;
	}

	public LocalDate getFechaRegistrada() {
		return fechaRegistrada;
	}

	public void setFechaRegistrada(LocalDate fechaRegistrada) {
		this.fechaRegistrada = fechaRegistrada;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public Usuario getMesero() {
		return mesero;
	}

	public void setMesero(Usuario mesero) {
		this.mesero = mesero;
	}
	
	
}


