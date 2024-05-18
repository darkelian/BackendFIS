package com.main.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.main.dtos.EmployeeResponse;
import com.main.dtos.StandardResponseDTO;
import com.main.security.JwtService;
import com.main.services.EmployeeService;
import com.main.services.UnitService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Data
@RestController
@RequestMapping("api/unit")
@AllArgsConstructor
@Tag(name = "Unidad de Servicios")
public class UnitController {
    private final UnitService unitService;
    private final EmployeeService employeeService;
    private final JwtService jwtService;

    @GetMapping("/employee")
    @Operation(summary = "Obtener empleados por unidad de servicio")
    public ResponseEntity<StandardResponseDTO> getEmployeeByServiceUnit(
            @RequestParam(required = false) String username) {
        boolean includeUnassigned = (username == null || username.trim().isEmpty());
        List<EmployeeResponse> employeeResponses = employeeService.getByServiceUnitEmployees(username,
                includeUnassigned);
        return ResponseEntity.ok(
                employeeResponses.isEmpty()
                        ? new StandardResponseDTO().failSuccess("No se encontraron empleados para " + username)
                        : new StandardResponseDTO().fullSuccess(employeeResponses));
    }

    @PutMapping("/employee/assign")
    @Operation(summary = "Asignar empleado a unidad de servicio")
    public ResponseEntity<StandardResponseDTO> assignEmployeeToUnit(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam Long employeeId) {
        String username = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);
            username = jwtService.getUsernameFromToken(jwtToken);
        }
        EmployeeResponse response = employeeService.assignServiceUnitToEmployee(employeeId, username);
        StandardResponseDTO successResponse = new StandardResponseDTO()
                .fullSuccess(response);
        return ResponseEntity.ok(successResponse);
    }
}
