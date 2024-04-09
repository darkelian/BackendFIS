package com.main.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.main.dtos.AdminRequest;
import com.main.dtos.EmployeeRequest;
import com.main.dtos.StandardResponseDTO;
import com.main.dtos.StudentRequest;
import com.main.services.UserService;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Data
@RestController
@RequestMapping("api/register")
@AllArgsConstructor
public class RegisterController {
    private final UserService userService;

    @PostMapping("/administrator")
    public ResponseEntity<StandardResponseDTO> registerAdministrator(@Validated @RequestBody AdminRequest request) {
        userService.registerAdministrator(request);
        StandardResponseDTO successResponse = new StandardResponseDTO()
                .fullSuccess("Usuario administrador registrado con exito");
        return ResponseEntity.ok(successResponse);
    }

    @PostMapping("/employee")
    public ResponseEntity<StandardResponseDTO> registerEmployee(@Validated @RequestBody EmployeeRequest request) {
        userService.registerEmployee(request);
        StandardResponseDTO successResponse = new StandardResponseDTO()
                .fullSuccess("Usuario empleado registrado con exito");
        return ResponseEntity.ok(successResponse);
    }

    @PostMapping("/student")
    public ResponseEntity<StandardResponseDTO> registerStudent(@Validated @RequestBody StudentRequest request) {
        StandardResponseDTO successResponse = new StandardResponseDTO()
                .fullSuccess("Estudiante registrado con exito");
        return ResponseEntity.ok(successResponse);
    }
}