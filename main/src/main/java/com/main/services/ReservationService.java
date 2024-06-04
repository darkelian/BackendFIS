package com.main.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.main.dtos.ReservationRequestDTO;
import com.main.dtos.ReservationResponseDTO;
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
            System.out.println("Cantidad disponible" + resource.getAvailableQuantity());
            System.out.println("Cantidad solicitada" + (reservedQuantity + request.getQuantity()));
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

    // Consultar todas las reservaciones de un estudiante
    @Transactional
    public List<ReservationResponseDTO> getReservationsByStudent(String username) {
        Student student = studentRepository.findByCodeStudent(Long.valueOf(username));
        if (student == null) {
            throw new ResourceNotFoundException("Estudiante no encontrado");
        }
        List<Reservation> reservations = reservationRepository.findByStudent(student);
        return convertToReservationsReponse(reservations);
    }

    public List<ReservationResponseDTO> convertToReservationsReponse(List<Reservation> reservations) {
        return reservations.stream().map(reservation -> {
            ReservationResponseDTO response = new ReservationResponseDTO();
            response.setId(reservation.getId());
            response.setDate(reservation.getDate());
            response.setStartTime(reservation.getStartTime());
            response.setEndTime(reservation.getEndTime());
            response.setStatus(reservation.getStatus());
            response.setReservationDate(reservation.getReservationDate());
            response.setQuantity(reservation.getQuantity());
            response.setEmployeeId(reservation.getEmployee().getId());
            response.setResourceId(reservation.getResource().getId());
            response.setStudentId(reservation.getStudent().getId());
            response.setResourceName(reservation.getResource().getName());
            response.setEmployeeName(reservation.getEmployee().getFirstName() + " "
                    + reservation.getEmployee().getFirstLastName() + " " + reservation.getEmployee().getMiddleName()
                    + " " + reservation.getEmployee().getMiddleLastName());
            response.setStudentName(reservation.getStudent().getFirstName() + " "
                    + reservation.getStudent().getFirstLastName() + " " + reservation.getStudent().getMiddleName()
                    + " " + reservation.getStudent().getMiddleLastName());
            return response;
        }).collect(Collectors.toList());
    }

    // Consultar el recurso más reservado por un estudiante
    @Transactional
    public String getMostReservedResource(String username) {
        Student student = studentRepository.findByCodeStudent(Long.valueOf(username));
        if (student == null) {
            throw new ResourceNotFoundException("Estudiante no encontrado");
        }
        List<Reservation> reservations = reservationRepository.findByStudent(student);
        if (reservations.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron reservaciones");
        }
        return reservations.stream().collect(Collectors.groupingBy(Reservation::getResource, Collectors.counting()))
                .entrySet().stream().max((entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue())).get()
                .getKey().getName();
    }

    // Consultar el recurso más reservado en el sistema
    @Transactional
    public String getMostReservedResource() {
        List<Reservation> reservations = reservationRepository.findAll();
        if (reservations.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron reservaciones");
        }
        return reservations.stream().collect(Collectors.groupingBy(Reservation::getResource, Collectors.counting()))
                .entrySet().stream().max((entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue())).get()
                .getKey().getName();
    }
}
