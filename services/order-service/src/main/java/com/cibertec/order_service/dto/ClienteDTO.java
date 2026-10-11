package com.cibertec.order_service.dto;

/**
 * 📌 DTO: ClienteDTO
 * 
 * Objeto de transferencia de datos para comunicar order-service con customer-service.
 * Se usa para validar clientes antes de crear pedidos.
 */
public class ClienteDTO {
    private Long idCliente;
    private String nombre;
    private String documento;
    private String tipoDocumento;
    private String direccion;
    private String correo;
    private String telefono;

    // Constructores
    public ClienteDTO() {}

    public ClienteDTO(Long idCliente, String nombre, String documento, String tipoDocumento, String direccion, String correo, String telefono) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.documento = documento;
        this.tipoDocumento = tipoDocumento;
        this.direccion = direccion;
        this.correo = correo;
        this.telefono = telefono;
    }

    // Getters y Setters
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

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
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
