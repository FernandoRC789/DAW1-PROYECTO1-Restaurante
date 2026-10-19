package com.cibertec.auth_service.event;

import java.util.List;

/**
 * Evento publicado por auth-service cuando se actualiza un usuario.
 */
public class UsuarioActualizadoEvento extends UsuarioEvento {

    private List<String> roles;

    public UsuarioActualizadoEvento() {
        super();
        this.tipoEvento = "USUARIO_ACTUALIZADO";
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
