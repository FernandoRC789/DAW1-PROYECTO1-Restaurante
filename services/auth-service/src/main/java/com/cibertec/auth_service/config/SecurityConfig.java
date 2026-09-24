package com.cibertec.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para pruebas en REST/Postman/Swagger
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/usuarios/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // Rutas libres
                .anyRequest().authenticated() // Lo demás requiere autenticación
            );
        return http.build();
    }
}
