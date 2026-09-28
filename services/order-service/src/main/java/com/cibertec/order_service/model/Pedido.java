package com.cibertec.order_service.model;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

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
	@Column(name = "id_estado")
	private Long estadoid;
	
	 /**
     * 🍽️ Mesa asociada al pedido
     */
	@NotNull(message = "Selecciona una mesa")
	@Column(name = "id_mesa")
	private Long mesaid;
	
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
	@Column(name = "id_mesero")
	private Long meseroid;
	
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
	
   


	public Pedido() {
	}

	public Pedido(Long idPedido, List<DetallePedido> detallePedido,
			@NotNull(message = "Selecciona un estado") Long estadoid,
			@NotNull(message = "Selecciona una mesa") Long mesaid,
			@NotNull(message = "Ingrese la fecha") @PastOrPresent LocalDate fechaRegistrada, String observaciones,
			@NotNull @DecimalMin("0.0") BigDecimal total, Long meseroid) {
		super();
		this.idPedido = idPedido;
		this.detallePedido = detallePedido;
		this.estadoid = estadoid;
		this.mesaid = mesaid;
		this.fechaRegistrada = fechaRegistrada;
		this.observaciones = observaciones;
		this.total = total;
		this.meseroid = meseroid;
	}

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

	public Long getEstadoid() {
		return estadoid;
	}

	public void setEstadoid(Long estadoid) {
		this.estadoid = estadoid;
	}

	public Long getMesaid() {
		return mesaid;
	}

	public void setMesaid(Long mesaid) {
		this.mesaid = mesaid;
	}

	public LocalDate getFechaRegistrada() {
		return fechaRegistrada;
	}

	public void setFechaRegistrada(LocalDate fechaRegistrada) {
		this.fechaRegistrada = fechaRegistrada;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public Long getMeseroid() {
		return meseroid;
	}

	public void setMeseroid(Long meseroid) {
		this.meseroid = meseroid;
	}

	// =========================
    // GETTERS / SETTERS
    // =========================
	
	
}


