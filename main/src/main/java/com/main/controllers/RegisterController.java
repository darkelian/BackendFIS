package com.main.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.main.dtos.StandardResponseDTO;
import com.main.services.UserService;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@Data
@RestController
@RequestMapping("api/register")
@AllArgsConstructor
public class RegisterController {
    private final UserService userService;

    @PostMapping("/administrator")
    public ResponseEntity<StandardResponseDTO> postMethodName(@RequestParam String password) {
        try {
            userService.registerAdministrator(password);
            StandardResponseDTO successResponse = new StandardResponseDTO().fullSuccess("Usuario administrador creado");
            return ResponseEntity.ok(successResponse);
        } catch (Exception ex) {
            return ResponseEntity.ok(new StandardResponseDTO().failSuccess("Credenciales invalidas"));
        }
    }
}