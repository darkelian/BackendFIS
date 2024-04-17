package com.main.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.dtos.EmployeeResponse;
import com.main.dtos.StandardResponseDTO;
import com.main.services.EmployeeService;
import com.main.services.StudentService;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Data
@RestController
@RequestMapping("api/admin")
@AllArgsConstructor
public class AdminController {
    private final StudentService studentService;
    private final EmployeeService employeeService;

    @GetMapping("/students")
    public ResponseEntity<StandardResponseDTO> getAllStudents() {
        StandardResponseDTO successResponse = new StandardResponseDTO()
                .fullSuccess(studentService.getAllStudents());
        return ResponseEntity.ok(successResponse);
    }

    @GetMapping("/employees")
    public ResponseEntity<StandardResponseDTO> getAllEmployees() {
        List<EmployeeResponse> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(new StandardResponseDTO(true, null, employees, employees.size()));
    }
}
