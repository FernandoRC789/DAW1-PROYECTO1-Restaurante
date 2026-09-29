package com.cibertec.api_gateway.filter;

import javax.crypto.SecretKey;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class JwtValidationFilter implements GlobalFilter, Ordered {

    // La misma llave secreta exacta que usas en tu auth-service
    private final String jwtSecret = "cibertec_secret_key_jwt_authentication_microservices_2026_secure";

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 1. Permitir que las rutas de login y registro pasen libremente sin token
        String path = request.getURI().getPath();
        if (path.contains("/api/auth/login") || path.contains("/api/auth/register")) {
            return chain.filter(exchange);
        }

        // 2. Verificar si la petición trae el encabezado de autorización
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return onError(exchange, "Falta el token de autorización", HttpStatus.UNAUTHORIZED);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Estructura de token inválida", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        try {
            // 3. VALIDACIÓN REAL DEL TOKEN usando la misma lógica de tu JwtUtils
            Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token);

        } catch (Exception e) {
            // Si el token expiró, fue alterado o la firma no coincide, cae aquí y rechaza la petición
            return onError(exchange, "Token no válido o expirado: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

        // 4. Si el token es legítimo, la petición avanza hacia billing, operations o catalog
        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -1; // Alta prioridad como aduana principal
    }
}
