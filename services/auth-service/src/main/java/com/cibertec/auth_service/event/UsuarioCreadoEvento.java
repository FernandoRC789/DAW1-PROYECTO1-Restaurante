package com.cibertec.auth_service.event;

import java.util.List;

/**
 * Evento publicado por auth-service cuando se crea un nuevo usuario.
 */
public class UsuarioCreadoEvento extends UsuarioEvento {

    private List<String> roles;

    public UsuarioCreadoEvento() {
        super();
        this.tipoEvento = "USUARIO_CREADO";
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
