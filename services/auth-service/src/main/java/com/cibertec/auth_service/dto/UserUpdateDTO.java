package com.cibertec.auth_service.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

/**
 * 📌 DTO: UserUpdateDTO
 * 
 * Usado para Actualizar usuarios
 */
public class UserUpdateDTO {

    private String username;
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
	public UserUpdateDTO(String username, String password, List<String> roles) {
		this.username = username;
		this.password = password;
		this.roles = roles;
	}
	public UserUpdateDTO() {
	}
}

