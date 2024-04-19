package com.main.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.dtos.ServiceUnitAvailabilityDTO;
import com.main.dtos.StandardResponseDTO;
import com.main.security.JwtService;
import com.main.services.ScheduleService;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@RestController
@RequestMapping("api/unit/shedule")
@AllArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final JwtService jwtService;

    @PostMapping("/availability")
    public ResponseEntity<StandardResponseDTO> registerAvailability(
            @RequestHeader("Authorization") String authorizationHeader,
            @Validated @RequestBody ServiceUnitAvailabilityDTO availabilityDTO) {
        String username = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);
            username = jwtService.getUsernameFromToken(jwtToken);
        }
        scheduleService.createSchedule(availabilityDTO, username);
        StandardResponseDTO successResponse = new StandardResponseDTO()
                .fullSuccess("Horario de disponibilidad registrado con exito");
        return ResponseEntity.ok(successResponse);
    }
}
