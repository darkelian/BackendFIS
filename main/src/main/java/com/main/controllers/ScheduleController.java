package com.main.controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

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

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;

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
        if (availabilityDTO.getAvailability().size() == 0
                || availabilityDTO.getAvailability().get(0).getDate() == null
                || availabilityDTO.getAvailability().get(0).getTimeSlots().size() == 0
                || !isValidDateFormat("dd/MM/yyyy", availabilityDTO.getAvailability().get(0).getDate())) {
            throw new DataIntegrityViolationException("No se puede registar la disponibilidad");
        }
        scheduleService.createSchedule(availabilityDTO, username);
        StandardResponseDTO successResponse = new StandardResponseDTO()
                .fullSuccess("Disponibilidad registrada exitosamente");
        return ResponseEntity.ok(successResponse);
    }

    @GetMapping("/availability")
    public ResponseEntity<StandardResponseDTO> getAvailability(@RequestParam String username) {
        StandardResponseDTO successResponse = new StandardResponseDTO();
        successResponse.fullSuccess(scheduleService.getScheduleByServiceUnitName(
                username));
        return ResponseEntity.ok(successResponse);
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
