package com.cibertec.auth_service.exception;

import java.time.LocalDateTime;

/*
 * Crear la respuesta estandarizada de Error
 * definirá la estructura JSON uniforme que enviará tu API cuando algo falle
 * */
public class ErrorDetails {

    private LocalDateTime timestamp;
    private int status;
    private String message;
    private String details;

    public ErrorDetails(LocalDateTime timestamp, int status, String message, String details) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.details = details;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public String getDetails() { return details; }
}