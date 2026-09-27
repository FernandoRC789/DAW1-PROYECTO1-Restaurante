package com.cibertec.auth_service.dto;

import java.util.List;

/**
 * 📌 DTO: UserResponseDTO
 * 
 * Objeto limpio para devolver información de usuarios al cliente (Frontend/Swagger)
 * sin exponer jamás la contraseña ni datos internos.(buena practica)
 */
public class UserResponseDTO {

    private Long idUser;
    private String username;
    private List<String> roles;

    public UserResponseDTO() {}

    public UserResponseDTO(Long idUser, String username, List<String> roles) {
        this.idUser = idUser;
        this.username = username;
        this.roles = roles;
    }

    // Getters y Setters
    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
