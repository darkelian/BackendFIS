package com.main.services;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.main.dtos.ReservationRequestDTO;
import com.main.exceptions.ResourceNotFoundException;
import com.main.models.Reservation;
import com.main.models.Resource;
import com.main.models.Student;
import com.main.models.Employee;
import com.main.repositories.ReservationRepository;
import com.main.repositories.ResourceRepository;
import com.main.repositories.StudentRepository;
import com.main.repositories.EmployeeRepository;

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
    private final EmployeeRepository employeeRepository;

    @Transactional
    public void createReservation(ReservationRequestDTO request, String username) {
        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Recurso no encontrado"));

        Student student = studentRepository.findByCodeStudent(Long.valueOf(username));
        if (student == null) {
            throw new ResourceNotFoundException("Estudiante no encontrado");
        }

        // Validar la disponibilidad del recurso
        List<Reservation> existingReservations = reservationRepository.findByResourceAndDate(resource, request.getDate());
        boolean isAvailable = existingReservations.stream()
                .noneMatch(reservation -> reservation.getStartTime().isBefore(request.getEndTime())
                        && reservation.getEndTime().isAfter(request.getStartTime()));

        if (!isAvailable) {
            throw new DataIntegrityViolationException("El recurso no está disponible en el rango horario solicitado");
        }

        if (resource.getAvailableQuantity() <= 0) {
            throw new DataIntegrityViolationException("No hay suficiente cantidad del recurso disponible");
        }

        // Asignar un empleado responsable
        Employee employee = findAvailableEmployee(resource);

        Reservation reservation = new Reservation();
        reservation.setResource(resource);
        reservation.setStudent(student);
        reservation.setEmployee(employee);
        reservation.setDate(request.getDate());
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setStatus("RESERVADO");
        reservationRepository.save(reservation);

        // Decrementar la cantidad disponible del recurso
        resource.setAvailableQuantity(resource.getAvailableQuantity() - 1);
        resourceRepository.save(resource);
    }

    private Employee findAvailableEmployee(Resource resource) {
        List<Employee> employees = employeeRepository.findEmployeesOrderByReservationCount();
        if (employees.isEmpty()) {
            throw new DataIntegrityViolationException("No hay empleados disponibles para asignar");
        }
        return employees.get(0);
    }

    @Transactional
    public void returnResource(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        reservation.setStatus("DISPONIBLE");
        reservationRepository.save(reservation);

        // Incrementar la cantidad disponible del recurso
        Resource resource = reservation.getResource();
        resource.setAvailableQuantity(resource.getAvailableQuantity() + 1);
        resourceRepository.save(resource);
    }
}
