package com.main.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.dtos.StandardResponseDTO;
import com.main.services.UnitService;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@RestController
@RequestMapping("api/unit")
@AllArgsConstructor
public class UnitController {
    private final UnitService unitService;

    @GetMapping("/")
    public ResponseEntity<StandardResponseDTO> getAllStudents() {
        StandardResponseDTO successResponse = new StandardResponseDTO()
                .fullSuccess(null);
        return ResponseEntity.ok(successResponse);
    }
}
