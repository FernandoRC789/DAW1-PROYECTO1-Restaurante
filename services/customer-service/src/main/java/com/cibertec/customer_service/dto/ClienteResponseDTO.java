package com.cibertec.customer_service.dto;

import com.cibertec.customer_service.model.TipoDocumento;

/**
 * DTO que define el contrato de salida de CUSTOMER SERVICE.
 */
public class ClienteResponseDTO {

    private Long idCliente;
    private String nombre;
    private String documento;
    private TipoDocumento tipoDocumento;
    private String direccion;
    private String correo;
    private String telefono;

    public ClienteResponseDTO() {
    }

    public ClienteResponseDTO(Long idCliente, String nombre, String documento,
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

    public String getNombre() {
        return nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }
}
