package com.cibertec.SistemaWebRestaurante.model;

import java.util.List;

import com.cibertec.SistemaWebRestaurante.utilsEnum.TipoDocumento;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 📌 ENTIDAD: Cliente
 * 
 * Representa a la persona o empresa a la que se le emite
 * un comprobante de pago (boleta o factura).
 * 
 * 🔥 Usado principalmente por:
 * - Cajero (registro de cliente)
 * - Comprobante de Pago
 * - Historial de ventas
 * 
 * 🔥 IMPORTANTE:
 * Para FACTURA normalmente se usa RUC
 * Para BOLETA normalmente DNI o cliente opcional
 */
@Entity
//@NoArgsConstructor
//@AllArgsConstructor
@Table(name = "tb_cliente")
public class Cliente {
	
    /**
     * 🔑 Identificador único del cliente
     * 
     * Se genera automáticamente en BD
     */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_cliente")
	private Long idCliente;
	
    /**
     * 👤 Nombre o razón social del cliente
     * 
     * Ejemplos:
     * - Juan Pérez
     * - Empresa SAC
     *     
     * ✔ Obligatorio
     */
	@NotBlank(message = "El nombre es obligatorio")
	@Column(name = "nom_cliente", nullable = false)
	private String nombre;
	
    /**
     * 🪪 Documento del cliente
     * 
     * Puede ser:
     * - DNI
     * - RUC
     * 
     * 🔥 Recomendado único
     */
    @NotBlank(message = "El documento es obligatorio")
    @Column(name = "doc_cliente", nullable = false, unique = true, length = 20)
    @Pattern(regexp = "\\d{8}|\\d{11}", message = "DNI debe tener 8 dígitos o RUC 11")
    private String documento;

    /**
     * 📄 Tipo de documento
     * 
     * Valores sugeridos:
     * - DNI
     * - RUC
     * - CE
     * 
     * 🔥 Recomendación:
     * usar ENUM más adelante
     */
    @NotNull(message = "Seleccione el tipo de documento")
    @Column(name = "tipo_documento")
	@Enumerated(EnumType.STRING)
    private TipoDocumento tipoDocumento;
	
    /**
     * 📍 Dirección del cliente
     * 
     * 🔥 Muy importante para facturas
     * Opcional en boletas
     */
    @Column(name = "dir_cliente", length = 150)
    private String direccion;
    
    /**
     * 📧 Correo electrónico
     * 
     * Opcional
     * útil para envío digital
     */
    @Column(name = "correo_cliente", length = 100)
    private String correo;

    /*
     * 📱telefono
     * */
    @Column(name = "tel_cliente", length = 15)
    private String telefono;
    
    /**
     * 🧾 Lista de comprobantes asociados al cliente
     * 
     * 🔥 Relación:
     * Un cliente puede tener varios comprobantes
     * 
     * JsonIgnore evita recursividad infinita en JSON
     */
    @OneToMany(mappedBy = "cliente")
    @JsonIgnore
    private List<ComprobanteDePago> comprobantes;

    
	// ====== CONSTRUCTOR CON PARAMETROS Y SIN PARAMETROS ======

    
    public Cliente(Long idCliente, @NotBlank(message = "El nombre es obligatorio") String nombre,
			@NotBlank(message = "El documento es obligatorio") @Pattern(regexp = "\\d{8}|\\d{11}", message = "DNI debe tener 8 dígitos o RUC 11") String documento,
			@NotNull(message = "Seleccione el tipo de documento") TipoDocumento tipoDocumento, String direccion,
			String correo, String telefono, List<ComprobanteDePago> comprobantes) {
		this.idCliente = idCliente;
		this.nombre = nombre;
		this.documento = documento;
		this.tipoDocumento = tipoDocumento;
		this.direccion = direccion;
		this.correo = correo;
		this.telefono = telefono;
		this.comprobantes = comprobantes;
	}
    

	public Cliente() {
	}


	//Getters y Setters

	public Long getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Long idCliente) {
		this.idCliente = idCliente;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public TipoDocumento getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(TipoDocumento tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public List<ComprobanteDePago> getComprobantes() {
		return comprobantes;
	}

	public void setComprobantes(List<ComprobanteDePago> comprobantes) {
		this.comprobantes = comprobantes;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
}
