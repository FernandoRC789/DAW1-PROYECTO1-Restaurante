package com.cibertec.SistemaWebRestaurante.model;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
@Table(name = "tb_usuario")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idUser;
	
	private String username;
	
	private String password;
	
	@ManyToMany
	@JoinTable(
		    name = "tb_usuario_roles",
		    joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "idUser"),
		    inverseJoinColumns = @JoinColumn(name = "rol_id", referencedColumnName = "idRol")
		)
	private Set<Rol> roles;
	
	@OneToMany(mappedBy = "mesero")
	@JsonIgnore
	private List<Pedido> pedidos;

	
	
	// =========================
    // CONSTRUCTOR CON PARAMETROS
	// Y SIN PARAMETROS
    // =========================
	public Usuario(Long idUser, String username, String password, Set<Rol> roles, List<Pedido> pedidos) {
		this.idUser = idUser;
		this.username = username;
		this.password = password;
		this.roles = roles;
		this.pedidos = pedidos;
	}

	public Usuario() {
	}

	// =========================
    // GETTERS / SETTERS
    // =========================
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Rol> getRoles() {
		return roles;
	}

	public void setRoles(Set<Rol> roles) {
		this.roles = roles;
	}

	public List<Pedido> getPedidos() {
		return pedidos;
	}

	public void setPedidos(List<Pedido> pedidos) {
		this.pedidos = pedidos;
	}
	
	
}
