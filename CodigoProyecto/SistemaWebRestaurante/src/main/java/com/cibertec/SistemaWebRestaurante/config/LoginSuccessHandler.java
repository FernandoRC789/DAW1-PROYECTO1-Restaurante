package com.cibertec.SistemaWebRestaurante.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
	                                    HttpServletResponse response,
	                                    Authentication authentication) throws IOException {

	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");

	    String rol = authentication.getAuthorities().iterator().next().getAuthority();

	    response.getWriter().write("{\"rol\": \"" + rol + "\"}");
	}
}