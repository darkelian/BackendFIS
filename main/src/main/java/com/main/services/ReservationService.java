package com.main.services;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.main.dtos.ReservationRequestDTO;
import com.main.exceptions.ResourceNotFoundException;
import com.main.models.Reservation;
import com.main.models.Resource;
import com.main.models.Student;
import com.main.repositories.ReservationRepository;
import com.main.repositories.ResourceRepository;
import com.main.repositories.StudentRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Service
@AllArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public void createReservation(ReservationRequestDTO request, String username) {
        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Recurso no encontrado"));

        //Student student = studentRepository.findById(username);
        // Validar la disponibilidad del recurso
        List<Reservation> existingReservations = reservationRepository.findByResourceAndDate(resource, request.getDate());
        boolean isAvailable = existingReservations.stream()
                .noneMatch(reservation -> reservation.getStartTime().isBefore(request.getEndTime())
                        && reservation.getEndTime().isAfter(request.getStartTime()));

        if (!isAvailable) {
            throw new DataIntegrityViolationException("El recurso no está disponible en el rango horario solicitado");
        }

        Reservation reservation = new Reservation();
        reservation.setResource(resource);
        reservation.setDate(request.getDate());
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setStatus("RESERVED");
        reservationRepository.save(reservation);
    }
}
