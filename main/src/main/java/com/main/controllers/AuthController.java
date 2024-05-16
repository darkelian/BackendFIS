package com.main.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.dtos.AuthResponse;
import com.main.dtos.LoginRequest;
import com.main.dtos.StandardResponseDTO;
import com.main.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Tag(name = "Autenticación")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<StandardResponseDTO> login(@Validated @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        StandardResponseDTO successResponse = new StandardResponseDTO().fullSuccess(authResponse);
        return ResponseEntity.ok(successResponse);
    }

}
