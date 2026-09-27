package com.cibertec.auth_service.dto;

import java.time.LocalDateTime;

/**
 * 📌 DTO: ErrorResponseDTO
 * 
 * Estructura estándar en JSON para notificar errores al cliente de forma profesional.
 */
public class ErrorResponseDTO {

    private String error;			// Detalles técnicos o ruta (ej: "uri=/api/usuarios/9")
    private LocalDateTime timestamp; // Hora exacta en que ocurrió el error
    private int status;              // Código HTTP (ej: 404, 400, 500)
    private String message;          // Mensaje descriptivo (ej: "Usuario no encontrado")

    public ErrorResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

 // Constructor para inicializar todos los datos del error
    public ErrorResponseDTO(int status, String error, String message) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
    }

 // Getters necesarios para que Spring Boot pueda convertir este objeto a JSON automáticamente
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getTimestamp() { return timestamp; }
}