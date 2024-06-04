package com.main.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.dtos.StandardResponseDTO;
import com.main.security.JwtService;
import com.main.services.ReservationService;
import com.main.utils.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Data
@RestController
@RequestMapping("api/student")
@AllArgsConstructor
@Tag(name = "Estudiantes")
public class StudentController {

    private final JwtService jwtService;
    private final ReservationService reservationService;

    @GetMapping("/reservations")
    @Operation(summary = "Obtener reservaciones de un estudiante")
    public ResponseEntity<StandardResponseDTO> getReservations(
            @RequestHeader("Authorization") String authorizationHeader) {
        String username = JwtUtil.extractUsernameFromHeader(jwtService, authorizationHeader);
        return ResponseEntity
                .ok(new StandardResponseDTO().fullSuccess(reservationService.getReservationsByStudent(username)));
    }

    @GetMapping("/reservation/most")
    @Operation(summary = "Obtener el recurso más reservado por un estudiante")
    public ResponseEntity<StandardResponseDTO> getMostReservedResource(
            @RequestHeader("Authorization") String authorizationHeader) {
        String username = JwtUtil.extractUsernameFromHeader(jwtService, authorizationHeader);
        return ResponseEntity.ok(new StandardResponseDTO().fullSuccess(reservationService.getMostReservedResource(username)));
    }

}
