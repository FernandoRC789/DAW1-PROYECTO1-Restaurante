package com.cibertec.SistemaWebRestaurante.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 📌 ENTIDAD: Comida
 * 
 * Representa los productos que el restaurante ofrece (platos, bebidas, etc.).
 * 
 * 🔥 Se relaciona con:
 * - Categoria (Muchos a Uno): cada comida pertenece a una categoría
 * - DetallePedido (Uno a Muchos): una comida puede estar en varios pedidos
 * 
 * 🔥 Usado por:
 * - Cocina (gestionar disponibilidad)
 * - Mesero (crear pedidos)
 * - Sistema (cálculo de ventas)
 */
@Entity
//@NoArgsConstructor
//@AllArgsConstructor
@Table(name = "tb_comida")
public class Comida {
	
    /**
     * 🔑 ID único de la comida (PK)
     * Auto incremental en base de datos
     */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_comida")
	private Long idComida;
	
    /**
     * 📝 Nombre del plato/comida
     * 
     * ✔ Obligatorio
     */
	@NotBlank(message = "El nombre es obligatorio")
	@Column(name = "nom_comida", nullable = false)
	private String nombre;
	
    /**
     * 📝 Descripción del plato
     * 
     * ✔ Obligatorio
     * ✔ Máximo 250 caracteres
     */
	@NotBlank(message = "Ingrese la descripcion de la comida")
	@Column(name = "des_comida", nullable = false, length = 250)
	private String descripcion;
	
    /**
     * 🏷️ Categoría de la comida
     * 
     * 🔥 Relación MANY TO ONE:
     * Muchas comidas pertenecen a una categoría
     * 
     * ⚠ Fetch.LAZY → mejora rendimiento
     * ⚠ JsonIgnore → evita errores de serialización infinita
     */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cat_id")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Categoria categoria;
	
    /**
     * 💰 Precio unitario
     * 
     * ✔ Obligatorio
     * ✔ Mínimo: 0.1
     * ✔ Precisión decimal controlada
     */
	@NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.1")
	@Column(name = "pre_comida", precision = 10, scale = 2)
	private BigDecimal precioUni;
	
    /**
     * 📦 Relación con detalle de pedidos
     * 
     * 🔥 Una comida puede estar en muchos pedidos
     * 
     * ⚠ JsonIgnore → evita loops infinitos en JSON
     */
	@OneToMany(mappedBy = "comida")
	@JsonIgnore
	private List<DetallePedido> detallePedido;
	
    /**
     * ✅ Estado de disponibilidad
     * 
     * true  → disponible
     * false → agotado
     * 
     * 🔥 Usado por cocina para activar/desactivar productos
     */
	@Column(name = "disponible")
	private boolean disponible = true;

	
	// ====== CONSTRUCTOR CON PARAMETROS Y SIN PARAMETROS ======

	
    public Comida(Long idComida, @NotBlank(message = "El nombre es obligatorio") String nombre,
			@NotBlank(message = "Ingrese la descripcion de la comida") String descripcion, Categoria categoria,
			@NotNull(message = "El precio es obligatorio") @DecimalMin("0.1") BigDecimal precioUni,
			List<DetallePedido> detallePedido, boolean disponible) {
		this.idComida = idComida;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.categoria = categoria;
		this.precioUni = precioUni;
		this.detallePedido = detallePedido;
		this.disponible = disponible;
	}

	public Comida() {
	}



	// getters y setters...
	public Long getIdComida() {
		return idComida;
	}

	public void setIdComida(Long idComida) {
		this.idComida = idComida;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public BigDecimal getPrecioUni() {
		return precioUni;
	}

	public void setPrecioUni(BigDecimal precioUni) {
		this.precioUni = precioUni;
	}

	public List<DetallePedido> getDetallePedido() {
		return detallePedido;
	}

	public void setDetallePedido(List<DetallePedido> detallePedido) {
		this.detallePedido = detallePedido;
	}

	public boolean isDisponible() {
		return disponible;
	}

	public void setDisponible(boolean disponible) {
		this.disponible = disponible;
	}
	
	
}

