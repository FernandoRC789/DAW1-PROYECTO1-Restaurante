package com.cibertec.auth_service.event;

import java.time.LocalDateTime;

/**
 * Clase base para todos los eventos de auditoría de usuarios publicados por
 * auth-service a través de RabbitMQ.
 */
public abstract class UsuarioEvento {

    protected Long idUsuario;
    protected String username;
    protected String tipoEvento;
    protected LocalDateTime fechaEvento;

    protected UsuarioEvento() {
        this.fechaEvento = LocalDateTime.now();
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(LocalDateTime fechaEvento) {
        this.fechaEvento = fechaEvento;
    }
}
