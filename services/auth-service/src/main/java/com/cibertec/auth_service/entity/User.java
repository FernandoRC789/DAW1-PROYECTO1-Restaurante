package com.cibertec.auth_service.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_users")
public class User {

	//declarando variables de la clase USER
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idUser;
	
	private String username;
	
	private String password;
	
	@ManyToMany
	@JoinTable(
		    name = "tb_users_roles",
		    joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "idUser"),
		    inverseJoinColumns = @JoinColumn(name = "rol_id", referencedColumnName = "idRole")
		)
	private Set<Role> roles = new HashSet<>();;
	
	//se elimino la dependencia de pedido eso se usara en el microservicio de ORDER.
}
