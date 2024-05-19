package com.main.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.main.dtos.ServiceUnitAvailabilityDTO;
import com.main.dtos.StandardResponseDTO;
import com.main.security.JwtService;
import com.main.services.ScheduleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;

@Data
@RestController
@RequestMapping("api/unit/schedule")
@AllArgsConstructor
@Tag(name = "Unidad de Servicios")
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final JwtService jwtService;

    @PostMapping("/availability")
    @Operation(summary = "Registrar disponibilidad horaria de la unidad de servicio")
    public ResponseEntity<StandardResponseDTO> registerAvailability(
            @RequestHeader("Authorization") String authorizationHeader,
            @Validated @RequestBody ServiceUnitAvailabilityDTO availabilityDTO) {
        String username = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);
            username = jwtService.getUsernameFromToken(jwtToken);
        }
        if (availabilityDTO.getAvailability().isEmpty()
                || availabilityDTO.getAvailability().get(0).getDate() == null
                || availabilityDTO.getAvailability().get(0).getTimeSlots().isEmpty()
                || !isValidDateFormat("dd/MM/yyyy", availabilityDTO.getAvailability().get(0).getDate())) {
            throw new DataIntegrityViolationException("No se puede registrar la disponibilidad");
        }
        scheduleService.createSchedule(availabilityDTO, username);
        StandardResponseDTO successResponse = new StandardResponseDTO()
                .fullSuccess("Disponibilidad registrada exitosamente");
        return ResponseEntity.ok(successResponse);
    }

    @GetMapping("/availability")
    @Operation(summary = "Obtener disponibilidad horaria de la unidad de servicio")
    public ResponseEntity<StandardResponseDTO> getAvailability(@RequestParam String username) {
        StandardResponseDTO successResponse = new StandardResponseDTO();
        return ResponseEntity.ok(scheduleService.getScheduleByServiceUnitName(username).getAvailability().isEmpty()
                ? successResponse.failSuccess("No hay disponibilidad horaria registrada")
                : successResponse.fullSuccess(scheduleService.getScheduleByServiceUnitName(username)));
    }

    private boolean isValidDateFormat(String format, String value) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date != null;
    }
}
