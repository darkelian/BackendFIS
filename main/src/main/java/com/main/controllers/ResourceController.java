package com.main.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.dtos.ResourceTypeDto;
import com.main.dtos.StandardResponseDTO;
import com.main.services.ResourceServices;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Data
@RestController
@RequestMapping("api/unit")
@AllArgsConstructor
public class ResourceController {
    private final ResourceServices resourceServices;

    @PostMapping("/type")
    public ResponseEntity<StandardResponseDTO> createTypeResource(@Validated @RequestBody ResourceTypeDto request) {
        return ResponseEntity.ok(new StandardResponseDTO().fullSuccess("Se creo"));
    }

}
