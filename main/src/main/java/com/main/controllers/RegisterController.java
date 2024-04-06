package com.main.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.main.dtos.EmployeeRequest;
import com.main.dtos.StandardResponseDTO;
import com.main.services.UserService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;

@Data
@RestController
@RequestMapping("api/register")
@AllArgsConstructor
public class RegisterController {
    private final UserService userService;

    @PostMapping("/administrator")
    public ResponseEntity<StandardResponseDTO> registerAdministrator(@RequestParam String password) {
        try {
            userService.registerAdministrator(password);
            StandardResponseDTO successResponse = new StandardResponseDTO()
                    .fullSuccess("Usuario administrador registrado con exito");
            return ResponseEntity.ok(successResponse);
        } catch (Exception ex) {
            return ResponseEntity
                    .ok(new StandardResponseDTO().failSuccess("No fue posible registrar un nuevo administrador"));
        }
    }

    @PostMapping("/employee")
    public ResponseEntity<StandardResponseDTO> registerEmployee(@Validated @RequestBody EmployeeRequest request) {
        userService.registerEmployee(request);
        StandardResponseDTO successResponse = new StandardResponseDTO()
                .fullSuccess("Usuario empleado registrado con exito");
        return ResponseEntity.ok(successResponse);
    }
}