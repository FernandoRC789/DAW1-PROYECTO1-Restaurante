package com.cibertec.auth_service.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

/**
 * 📌 DTO: UserCreateDTO
 * 
 * Usado para crear USUARIOS
 */
//@Data
public class UserCreateDTO {

	@NotBlank(message = "El username es obligatorio")
    private String username;
	@NotBlank(message = "La contraseña es obligatoria")
    private String password;
    private List<String> roles;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	public UserCreateDTO(String username, String password, List<String> roles) {
		this.username = username;
		this.password = password;
		this.roles = roles;
	}
	public UserCreateDTO() {
	}
}