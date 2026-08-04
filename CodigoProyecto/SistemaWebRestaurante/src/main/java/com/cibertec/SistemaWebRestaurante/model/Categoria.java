package com.cibertec.SistemaWebRestaurante.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 📌 Entidad que representa una categoría de comidas
 * 
 * Ejemplos:
 * - Entradas
 * - Platos de fondo
 * - Bebidas
 * - Postres
 * 
 * Relación:
 * - Una categoría puede tener muchas comidas (1:N)
 */
@Entity
//@NoArgsConstructor
//@AllArgsConstructor
@Table(name = "tb_categoria")
public class Categoria {
	
    /**
     * 🔑 Identificador único de la categoría
     */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_cat")
	private Long idCat;
	
    /**
     * 📌 Nombre de la categoría
     * 
     * 🔥 Validación:
     * - No puede estar vacío
     * - Unico evitar duplicados con unique en true
     */
	@NotBlank(message = "El nombre es obligatorio")
	@Column(name = "nom_cat", nullable = false,  unique = true)
	private String nombre;
	
    /**
     * 🔗 Relación con comidas
     * 
     * Una categoría puede tener múltiples comidas
     * 
     * 🔥 IMPORTANTE:
     * - mappedBy indica que la relación es controlada por Comida
     * - @JsonIgnore evita recursividad infinita en JSON
     */
	@OneToMany(mappedBy = "categoria")
	@JsonIgnore // 🔥 ESTA ES LA CLAVE
	private List<Comida> comidas;

	// ====== CONSTRUCTORES CON PARAMETROS Y SIN PARAMETROS ======
	
    public Categoria(Long idCat, @NotBlank(message = "El nombre es obligatorio") String nombre, List<Comida> comidas) {
		this.idCat = idCat;
		this.nombre = nombre;
		this.comidas = comidas;
	}
    
    

	public Categoria() {
	}



	// ====== GETTERS Y SETTERS ======
	public Long getIdCat() {
		return idCat;
	}

	public void setIdCat(Long idCat) {
		this.idCat = idCat;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Comida> getComidas() {
		return comidas;
	}

	public void setComidas(List<Comida> comidas) {
		this.comidas = comidas;
	}
}