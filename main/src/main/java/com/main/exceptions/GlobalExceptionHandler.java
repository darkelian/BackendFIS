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

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Manejo de errores de validación de campos
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        StandardResponseDTO response = new StandardResponseDTO(false, null, null, errors, errors.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Manejo de errores de autenticación
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<StandardResponseDTO> handleAuthenticationException(AuthenticationException ex) {
        List<String> errors = List.of(ex.getMessage());
        StandardResponseDTO response = new StandardResponseDTO(false, null, null, errors, 1);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Manejo de errores de credenciales inválidas
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardResponseDTO> handleBadCredentialsException(BadCredentialsException ex) {
        List<String> errors = List.of("Credenciales inválidas. Por favor, intente nuevamente.");
        StandardResponseDTO response = new StandardResponseDTO(false, null, null, errors, 1);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Manejo de errores de integridad de datos
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardResponseDTO> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex) {
        List<String> errors = List.of("Error de integridad de datos: " + ex.getMostSpecificCause().getMessage());
        StandardResponseDTO response = new StandardResponseDTO(false, null, null, errors, 1);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Manejo de errores de recurso no encontrado
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardResponseDTO> handleResourceNotFoundException(ResourceNotFoundException ex) {
        List<String> errors = List.of(ex.getMessage());
        StandardResponseDTO response = new StandardResponseDTO(false, null, null, errors, 1);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
