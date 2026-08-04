package com.cibertec.SistemaWebRestaurante.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {
	

    private final LoginSuccessHandler successHandler;

    public SecurityConfig(LoginSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }
	
    /*@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // 🔥 TODO LIBRE
            );

        return http.build();
    }*/
    
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration config = new CorsConfiguration();

	    config.setAllowedOrigins(List.of("http://localhost:4200"));
	    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
	    config.setAllowedHeaders(List.of("*"));
	    config.setAllowCredentials(true); // 🔥 CLAVE

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", config);

	    return source;
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	    http
        .csrf(csrf -> csrf.disable()) // 🔥 CLAVE
        .cors(cors -> {}) // 🔥 ESTA ES LA FORMA CORRECTA
        
	    .authorizeHttpRequests(auth -> auth
	        .requestMatchers("/publico", "/login").permitAll()
	        .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()

	        .requestMatchers("/mesero","/api/pedidos/**","/api/mesas/**").hasAnyRole("MESERO","ADMIN","COCINA","CAJERO")
	        .requestMatchers("/caja","/api/comprobantes/**","/api/clientes").hasAnyRole("CAJERO","ADMIN")
	        .requestMatchers("/cocina","/api/comidas/**").hasAnyRole("COCINA","ADMIN","MESERO")
	        .requestMatchers("/admin","/api/usuarios/**").hasRole("ADMIN")


	        .anyRequest().authenticated()
	    )
	    .formLogin(form -> form
	    	.loginProcessingUrl("/login") // 🔥 ESTE ES EL CORRECTO
	        .successHandler((AuthenticationSuccessHandler) successHandler)
	        .permitAll()
	    )
	    .logout(logout -> logout
	    	    .logoutUrl("/logout")
	    	    .logoutSuccessHandler((request, response, authentication) -> {
	    	        response.setStatus(HttpServletResponse.SC_OK);
	    	    })
	    	    .permitAll()
	    	);

	    return http.build();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	//Necesario si usas AuthenticationManager
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
		return config.getAuthenticationManager();
	}

}
