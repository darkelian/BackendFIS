package com.main.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.dtos.EmployeeResponse;
import com.main.dtos.ServicesUnitResponse;
import com.main.dtos.StandardResponseDTO;
import com.main.services.EmployeeService;
import com.main.services.StudentService;
import com.main.services.UnitService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Data
@RestController
@RequestMapping("api/admin")
@AllArgsConstructor
@Tag(name = "Administrador")
public class AdminController {
    private final StudentService studentService;
    private final EmployeeService employeeService;
    private final UnitService unitService;

    @GetMapping("/students")
    @Operation(summary = "Obtener todos los estudiantes")
    public ResponseEntity<StandardResponseDTO> getAllStudents() {
        StandardResponseDTO successResponse = new StandardResponseDTO()
                .fullSuccess(studentService.getAllStudents());
        return ResponseEntity.ok(successResponse);
    }

    @GetMapping("/employees")
    @Operation(summary = "Obtener todos los empleados")
    public ResponseEntity<StandardResponseDTO> getAllEmployees() {
        List<EmployeeResponse> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(new StandardResponseDTO(true, null, employees, null, employees.size()));
    }

    @GetMapping("/units")
    @Operation(summary = "Obtener todas las unidades de servicio")
    public ResponseEntity<StandardResponseDTO> getAllUnitService() {
        List<ServicesUnitResponse> unit = unitService.getAllServicesUnit();
        return ResponseEntity.ok(new StandardResponseDTO(true, null, unit, null, unit.size()));
    }
}
