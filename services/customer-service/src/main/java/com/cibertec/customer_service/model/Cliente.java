package com.cibertec.customer_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Cliente perteneciente exclusivamente al dominio CUSTOMER.
 *
 * Se conserva la estructura del Cliente del monolito:
 * idCliente, nombre, documento, tipoDocumento, direccion, correo y telefono.
 *
 * No contiene relaciones JPA con Pedido, Comprobante, Usuario u otras
 * entidades de otros microservicios. Esto respeta la propiedad de datos
 * definida en la arquitectura de microservicios.
 */
@Entity
@Table(name = "tb_cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idCliente;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nom_cliente", nullable = false)
    private String nombre;

    @NotBlank(message = "El documento es obligatorio")
    @Pattern(regexp = "\\d{8}|\\d{11}", message = "DNI debe tener 8 dígitos o RUC 11")
    @Column(name = "doc_cliente", nullable = false, unique = true, length = 20)
    private String documento;

    @NotNull(message = "Seleccione el tipo de documento")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "dir_cliente", length = 150)
    private String direccion;

    @Column(name = "correo_cliente", length = 100)
    private String correo;

    @Column(name = "tel_cliente", length = 15)
    private String telefono;

    public Cliente() {
    }

    public Cliente(Long idCliente, String nombre, String documento,
                   TipoDocumento tipoDocumento, String direccion,
                   String correo, String telefono) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.documento = documento;
        this.tipoDocumento = tipoDocumento;
        this.direccion = direccion;
        this.correo = correo;
        this.telefono = telefono;
    }

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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
