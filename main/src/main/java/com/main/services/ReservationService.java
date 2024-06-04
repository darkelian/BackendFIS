package com.main.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.main.dtos.ReservationRequestDTO;
import com.main.exceptions.ResourceNotFoundException;
import com.main.models.Reservation;
import com.main.models.Resource;
import com.main.models.ResourceStatus;
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
    public String createReservation(ReservationRequestDTO request, String username) {
        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Recurso no encontrado"));

        Student student = studentRepository.findByCodeStudent(Long.valueOf(username));
        if (student == null) {
            throw new ResourceNotFoundException("Estudiante no encontrado");
        }

        // Validar la disponibilidad del recurso basado en la cantidad solicitada
        List<Reservation> existingReservations = reservationRepository.findByResourceAndDate(resource,
                request.getDate());
        int reservedQuantity = existingReservations.stream()
                .filter(reservation -> reservation.getStartTime().isBefore(request.getEndTime())
                        && reservation.getEndTime().isAfter(request.getStartTime()))
                .mapToInt(Reservation::getQuantity)
                .sum();

        if (request.getQuantity() > resource.getAvailableQuantity()) {
            System.out.println("Cantidad disponible"+resource.getAvailableQuantity());
            System.out.println("Cantidad solicitada"+(reservedQuantity + request.getQuantity()));
            throw new DataIntegrityViolationException("El recurso no está disponible en la cantidad solicitada");
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
        reservation.setQuantity(request.getQuantity());
        reservation.setReservationDate(LocalDate.now());
        reservationRepository.save(reservation);

        // Decrementar la cantidad disponible del recurso
        resource.setAvailableQuantity(resource.getAvailableQuantity() - request.getQuantity());
        if (resource.getAvailableQuantity() == 0) {
            resource.setStatus(ResourceStatus.RESERVADO);
        }
        resourceRepository.save(resource);
        return resource.getName();
    }

    private Employee findAvailableEmployee(Resource resource) {
        List<Employee> employees = employeeRepository.findEmployeesOrderByReservationCount();
        if (employees.isEmpty()) {
            throw new DataIntegrityViolationException("No hay empleados disponibles para asignar");
        }
        return employees.get(0);
    }
}
