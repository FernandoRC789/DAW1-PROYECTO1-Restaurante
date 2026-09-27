package com.cibertec.auth_service.security;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

    // Clave secreta muy segura para firmar el token (en producción va en application.properties)
    private final String jwtSecret = "cibertec_secret_key_jwt_authentication_microservices_2026_secure";
    private final int jwtExpirationMs = 86400000; // 1 día de duración

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // 1. Generar el Token JWT a partir de la autenticación
    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        // Extraemos los roles del usuario para guardarlos dentro del token
        String roles = userPrincipal.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("roles", roles) // Guardamos los roles en el payload del token
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSignKey())
                .compact();
    }

    // 2. Extraer el username del token
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 3. Validar que el token sea legítimo y no haya expirado
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) getSignKey())
                .build()
                .parseSignedClaims(authToken);
            return true;
        } catch (Exception e) {
            System.err.println("Error de validación del JWT: " + e.getMessage());
        }
        return false;
    }
}