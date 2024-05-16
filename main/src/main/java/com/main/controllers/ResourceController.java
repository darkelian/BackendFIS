package com.main.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.dtos.ResourceTypeDto;
import com.main.dtos.StandardResponseDTO;
import com.main.models.ServiceUnit;
import com.main.security.JwtService;
import com.main.services.ResourceServices;
import com.main.services.ScheduleService;
import com.main.services.TypeResourceService;
import com.main.services.UnitService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Data
@RestController
@RequestMapping("api/resources")
@AllArgsConstructor
@Tag(name = "Unidad de Servicios")
public class ResourceController {
    private final ResourceServices resourceServices;
    private final ScheduleService scheduleService;
    private final JwtService jwtService;
    private final TypeResourceService typeResourceService;
    private final UnitService unitService;

    @PostMapping("/type")
    @Operation(summary = "Crear un nuevo tipo de recurso")
    public ResponseEntity<StandardResponseDTO> createTypeResource(
            @RequestHeader("Authorization") String authorizationHeader,
            @Validated @RequestBody ResourceTypeDto request) {
        String username = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);
            username = jwtService.getUsernameFromToken(jwtToken);
        }

        if (scheduleService.getScheduleByServiceUnitName(username).getAvailability().isEmpty()) {
            throw new DataIntegrityViolationException("Sin disponibilidad, agregar una disponibilidad");
        }

        ServiceUnit serviceUnit = unitService.getServicesUnitByUsername(username)
                .orElseThrow(() -> new IllegalStateException(
                        "No se encontró la unidad de servicio para el usuario proporcionado"));

        typeResourceService.createTypeResource(request, serviceUnit);

        StandardResponseDTO response = new StandardResponseDTO().fullSuccess("Recurso tipo creado con éxito");
        return ResponseEntity.ok(response);
    }
}
