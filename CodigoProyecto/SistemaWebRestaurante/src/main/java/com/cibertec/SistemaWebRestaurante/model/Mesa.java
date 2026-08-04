package com.cibertec.SistemaWebRestaurante.model;

import java.util.List;

import com.cibertec.SistemaWebRestaurante.utilsEnum.EstadoMesa;
import com.cibertec.SistemaWebRestaurante.utilsEnum.TipoComprobante;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
//@NoArgsConstructor
//@AllArgsConstructor
@Table(name = "tb_mesa")
public class Mesa {
	
    /**
     * 🆔 ID único de la mesa
     */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_mesa")
	private Long idMesa;
	
    /**
     * 🔢 Número de mesa (visible en el restaurante)
     * 
     * 📌 Reglas:
     * - Obligatorio
     * - Máximo 2 caracteres (ej: "1", "10")
     * - Único (no puede repetirse)
     */
	@NotBlank(message = "El numero es obligatorio")
    @Size(min = 1, max = 2, message = "El número de mesa debe tener máximo 2 dígitos") // 🔥 CORREGIDO
	@Column(name = "num_mesa", length = 2, nullable = false, unique = true)
	private String numero;
	
    /**
     * 🪑 Cantidad de asientos
     * 
     * 📌 Reglas:
     * - Mínimo 1 persona
     * - Máximo 10 personas
     */
	@NotNull(message = "La cantidad de asientos es obligatorio")
	@Min(value = 1, message = "La mesa debe tener al menos 1 asiento")
	@Max(value = 10, message = "No creo que tengas una mesa para más de 10 personas")
	@Column(name = "asiento_mesa", nullable = false)
	private int asientos;
	
    /**
     * 📋 Relación: Mesa → Pedidos
     * 
     * Una mesa puede tener múltiples pedidos.
     * 
     * 🔥 IMPORTANTE:
     * - Se usa @JsonIgnore para evitar bucles infinitos en JSON
     */
	@OneToMany(mappedBy = "mesa")
	@JsonIgnore
	private List<Pedido> pedidos;
	
    // =========================
    // 🔥 MEJORAS OPCIONALES
    // =========================

    /**
     * 🆕 Estado de la mesa (opcional pero MUY útil)
     * 
     * Ejemplo:
     * - LIBRE
     * - OCUPADA
     * - RESERVADA
     */
	@Column(name = "estado_mesa")
	@Enumerated(EnumType.STRING)
	private EstadoMesa estado_mesa = EstadoMesa.LIBRE; // ✅ default /  LIBRE,OCUPADO,RESERVADO, NO_DISPONIBLE

	
	// =========================
    // CONSTRUCTOR CON PARAMETROS Y
	// SIN PARAMETROS
    // =========================
	
    public Mesa(Long idMesa,
			@NotBlank(message = "El numero es obligatorio") @Size(min = 1, max = 2, message = "El número de mesa debe tener máximo 2 dígitos") String numero,
			@NotNull(message = "La cantidad de asientos es obligatorio") @Min(value = 1, message = "La mesa debe tener al menos 1 asiento") @Max(value = 10, message = "No creo que tengas una mesa para más de 10 personas") int asientos,
			List<Pedido> pedidos, EstadoMesa estado_mesa) {
		this.idMesa = idMesa;
		this.numero = numero;
		this.asientos = asientos;
		this.pedidos = pedidos;
		this.estado_mesa = estado_mesa;
	}

	public Mesa() {
	}



	// =========================
    // GETTERS / SETTERS
    // =========================
	public Long getIdMesa() {
		return idMesa;
	}

	public void setIdMesa(Long idMesa) {
		this.idMesa = idMesa;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public int getAsientos() {
		return asientos;
	}

	public void setAsientos(int asientos) {
		this.asientos = asientos;
	}

	public List<Pedido> getPedidos() {
		return pedidos;
	}

	public void setPedidos(List<Pedido> pedidos) {
		this.pedidos = pedidos;
	}

	public EstadoMesa getEstado_mesa() {
		return estado_mesa;
	}

	public void setEstado_mesa(EstadoMesa estado_mesa) {
		this.estado_mesa = estado_mesa;
	}


	
}