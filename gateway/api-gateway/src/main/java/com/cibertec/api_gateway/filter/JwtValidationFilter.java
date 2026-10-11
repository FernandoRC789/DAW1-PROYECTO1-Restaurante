package com.cibertec.api_gateway.filter;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.netty.handler.codec.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Filtro de validación JWT para el API Gateway
 * Valida el token JWT en todas las peticiones excepto login y registro
 */
@Component
public class JwtValidationFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(JwtValidationFilter.class);
    
    // La misma llave secreta exacta que usas en tu auth-service
    private final String jwtSecret = "cibertec_secret_key_jwt_authentication_microservices_2026_secure";
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Permitir solicitudes OPTIONS (Preflight de CORS del navegador) sin restricciones
        if (request.getMethod().equals(HttpMethod.OPTIONS)) {
            return chain.filter(exchange);
        }
        
        // Permitir que las rutas de login y registro pasen libremente sin token
        String path = request.getURI().getPath();
        if (path.contains("/api/auth/login") || path.contains("/api/auth/register")) {
            return chain.filter(exchange);
        }

        // Verificar si la petición trae el encabezado de autorización
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return onError(exchange, "Falta el token de autorización", HttpStatus.UNAUTHORIZED);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Estructura de token inválida", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        try {
            // VALIDACIÓN REAL DEL TOKEN usando la misma lógica de tu JwtUtils
            Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token);

        } catch (Exception e) {
            logger.error("Error validando token JWT: {}", e.getMessage());
            return onError(exchange, "Token no válido o expirado: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

        // Si el token es legítimo, la petición avanza hacia billing, operations o catalog
        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> errorResponse = Map.of(
            "timestamp", java.time.LocalDateTime.now().toString(),
            "status", httpStatus.value(),
            "error", httpStatus.getReasonPhrase(),
            "message", err,
            "path", exchange.getRequest().getPath().value()
        );
        
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            logger.error("Error escribiendo respuesta de error: {}", e.getMessage());
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1; // Alta prioridad como aduana principal
    }
}
