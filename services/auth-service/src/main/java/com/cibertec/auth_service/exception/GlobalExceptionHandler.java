package com.cibertec.auth_service.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

//captura las excepciones de cualquier controlador de el proyecto
@RestControllerAdvice
public class GlobalExceptionHandler {

	// Intercepta exclusivamente cuando lanzas un ResourceNotFoundException
    // 1. Manejar Recurso No Encontrado (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

    	// Construye el molde JSON con los datos del error 404
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(), // El mensaje que pusiste en el Service
                request.getDescription(false) // La URI de la petición que falló
        );
     // Devuelve el JSON empaquetado con un código HTTP 404 real
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
    
    // Intercepta cuando lanzas un BadRequestException (Errores de negocio)
    // 2. Manejar Errores de Negocio / Petición Incorrecta (400)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDetails> handleBadRequestException(
            BadRequestException ex, WebRequest request) {

        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // 3. Manejar Errores de Validaciones de Anotaciones (@NotBlank, etc.)
    // Intercepta los errores automáticos de validación de los DTOs (@NotBlank, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
     // Recorre todos los campos que fallaron la validación y extrae sus mensajes
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // 4. Manejar cualquier otro error no previsto (500)
    // Red de seguridad final: Intercepta cualquier otro error inesperado en el sistema
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(
            Exception ex, WebRequest request) {

        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocurrió un error interno en el servidor",
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}