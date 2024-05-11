package com.main.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.main.dtos.StandardResponseDTO;
import com.main.services.EmployeeService;
import com.main.services.UnitService;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.PutMapping;

@Data
@RestController
@RequestMapping("api/unit")
@AllArgsConstructor
public class UnitController {
    private final UnitService unitService;
    private final EmployeeService employeeService;

    @GetMapping("/employee")
    public ResponseEntity<StandardResponseDTO> getEmployeeByServiceUnit(@RequestParam String username) {
        boolean assign = username.equalsIgnoreCase(username);
        return ResponseEntity
                .ok(new StandardResponseDTO().fullSuccess(employeeService.getByServiceUnitEmployees(username, assign)));
    }

    @PutMapping("/employee/assign")
    public ResponseEntity<StandardResponseDTO> assignEmployeeToUnit() {
        return ResponseEntity.ok(new StandardResponseDTO().fullSuccess(null));
    }
}
