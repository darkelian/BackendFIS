package com.main.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;

import com.main.dtos.StandardResponseDTO;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Manejo de errores de validación de campos
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        StandardResponseDTO response = new StandardResponseDTO();
        response.setSuccess(false);
        response.setErrors(errors);
        response.setCount(errors.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Manejo de errores de autenticación
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<StandardResponseDTO> handleAuthenticationException(AuthenticationException ex) {
        List<String> errors = List.of(ex.getMessage());
        StandardResponseDTO response = new StandardResponseDTO();
        response.setSuccess(false);
        response.setErrors(errors);
        response.setCount(1);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Manejo de errores de credenciales inválidas
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardResponseDTO> handleBadCredentialsException(BadCredentialsException ex) {
        List<String> errors = new ArrayList<>();
        errors.add("Credenciales inválidas. Por favor, intente nuevamente.");
        StandardResponseDTO response = new StandardResponseDTO();
        response.setErrors(errors);
        response.setSuccess(false);
        response.setCount(1);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Manejo de errores de integridad de datos
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardResponseDTO> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("timestamp", Instant.now());
        errors.put("status", HttpStatus.CONFLICT.value());
        errors.put("errors", ex.getMostSpecificCause().getMessage());
        StandardResponseDTO response = new StandardResponseDTO();
        response.setSuccess(false);
        // response.setErrors(errors);
        response.setCount(1);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Manejo de errores de recurso no encontrado
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardResponseDTO> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("timestamp", Instant.now());
        errors.put("status", HttpStatus.NOT_FOUND.value());
        errors.put("errors", ex.getMessage());
        StandardResponseDTO response = new StandardResponseDTO();
        response.setSuccess(false);
        // response.setErrors(errors);
        response.setCount(1);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
