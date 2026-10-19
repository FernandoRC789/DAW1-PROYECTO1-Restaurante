package com.cibertec.auth_service.event;

/**
 * Evento publicado por auth-service cuando se elimina un usuario.
 */
public class UsuarioEliminadoEvento extends UsuarioEvento {

    public UsuarioEliminadoEvento() {
        super();
        this.tipoEvento = "USUARIO_ELIMINADO";
    }
}
