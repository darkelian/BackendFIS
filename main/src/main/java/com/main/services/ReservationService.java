package com.main.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.main.dtos.EmployeeReservationResponseDTO;
import com.main.dtos.ReservationRequestDTO;
import com.main.dtos.ReservationResponseDTO;
import com.main.dtos.ServiceUnitAvailabilityDTO;
import com.main.exceptions.ResourceNotFoundException;
import com.main.models.Reservation;
import com.main.models.Resource;
import com.main.models.ResourceStatus;
import com.main.models.ServiceUnit;
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
    private final ScheduleService scheduleService; // Suponiendo que este es el servicio que tiene getScheduleByServiceUnitName

    @Transactional
    public String createReservation(ReservationRequestDTO request, String username) {
        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Recurso no encontrado"));

        Student student = studentRepository.findByCodeStudent(Long.valueOf(username));
        if (student == null) {
            throw new ResourceNotFoundException("Estudiante no encontrado");
        }

        validateResourceAvailability(resource, request);

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

        updateResourceAvailability(resource, request.getQuantity());
        return resource.getName();
    }

    private void validateResourceAvailability(Resource resource, ReservationRequestDTO request) {
        // Validar disponibilidad del recurso basado en cantidad solicitada
        List<Reservation> existingReservations = reservationRepository.findByResourceAndDate(resource,
                request.getDate());
        int reservedQuantity = existingReservations.stream()
                .filter(reservation -> reservation.getStartTime().isBefore(request.getEndTime())
                        && reservation.getEndTime().isAfter(request.getStartTime()))
                .mapToInt(Reservation::getQuantity)
                .sum();

        if (reservedQuantity + request.getQuantity() > resource.getAvailableQuantity()) {
            throw new DataIntegrityViolationException("El recurso no está disponible en la cantidad solicitada");
        }

        // Validar rango horario de la unidad de servicio
        ServiceUnit serviceUnit = resource.getType().getServiceUnit();
        ServiceUnitAvailabilityDTO availabilityDTO = scheduleService.getScheduleByServiceUnitName(serviceUnit.getName());
        boolean isWithinSchedule = availabilityDTO.getAvailability().stream()
                .anyMatch(dayAvailability -> 
                    LocalDate.parse(dayAvailability.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")).equals(request.getDate()) &&
                    dayAvailability.getTimeSlots().stream().anyMatch(slot ->
                        !request.getStartTime().isBefore(LocalTime.parse(slot.getStartTime())) &&
                        !request.getEndTime().isAfter(LocalTime.parse(slot.getEndTime()))
                    )
                );

        if (!isWithinSchedule) {
            throw new DataIntegrityViolationException(
                    "El horario de la reservación está fuera del rango horario permitido por la unidad de servicio");
        }
    }

    private void updateResourceAvailability(Resource resource, int quantity) {
        resource.setAvailableQuantity(resource.getAvailableQuantity() - quantity);
        if (resource.getAvailableQuantity() == 0) {
            resource.setStatus(ResourceStatus.RESERVADO);
        }
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
    public List<ReservationResponseDTO> getReservationsByStudent(String username) {
        Student student = studentRepository.findByCodeStudent(Long.valueOf(username));
        if (student == null) {
            throw new ResourceNotFoundException("Estudiante no encontrado");
        }
        List<Reservation> reservations = reservationRepository.findByStudentAndStatus(student, "RESERVADO");
        return convertToReservationsResponse(reservations);
    }

    public List<ReservationResponseDTO> convertToReservationsResponse(List<Reservation> reservations) {
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
            response.setEmployeeName(String.format("%s %s %s %s",
                    reservation.getEmployee().getFirstName(),
                    reservation.getEmployee().getFirstLastName(),
                    reservation.getEmployee().getMiddleName(),
                    reservation.getEmployee().getMiddleLastName()));
            response.setStudentName(String.format("%s %s %s %s",
                    reservation.getStudent().getFirstName(),
                    reservation.getStudent().getFirstLastName(),
                    reservation.getStudent().getMiddleName(),
                    reservation.getStudent().getMiddleLastName()));
            return response;
        }).collect(Collectors.toList());
    }

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
                .entrySet().stream().max(Map.Entry.comparingByValue()).get()
                .getKey().getName();
    }

    @Transactional
    public String getMostReservedResource() {
        List<Reservation> reservations = reservationRepository.findAll();
        if (reservations.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron reservaciones");
        }
        return reservations.stream().collect(Collectors.groupingBy(Reservation::getResource, Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue()).get()
                .getKey().getName();
    }

    @Transactional
    public String getMostReservedResourceByTypeAndDateRange(String startDateStr, String endDateStr,
            String resourceType) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);

        List<Reservation> reservations = reservationRepository.findByResourceTypeAndDateBetween(resourceType, startDate,
                endDate);
        if (reservations.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron reservaciones");
        }

        return reservations.stream()
                .collect(Collectors.groupingBy(Reservation::getResource, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey()
                .getName();
    }

    @Transactional
    public void changeReservationToLoan(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservación no encontrada"));
        if (!reservation.getStatus().equals("RESERVADO")) {
            throw new DataIntegrityViolationException("La reservación no está en estado de reservado");
        }
        reservation.setStatus("PRESTADO");
        reservationRepository.save(reservation);
    }

    @Transactional
    public void changeLoanToReturned(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservación no encontrada"));
        if (!reservation.getStatus().equals("PRESTADO")) {
            throw new DataIntegrityViolationException("La reservación no está en estado de prestado");
        }

        Resource resource = reservation.getResource();
        resource.setAvailableQuantity(resource.getAvailableQuantity() + reservation.getQuantity());
        if (resource.getStatus().equals(ResourceStatus.RESERVADO)) {
            resource.setStatus(ResourceStatus.DISPONIBLE);
        }
        resourceRepository.save(resource);

        reservation.setStatus("DISPONIBLE");
        reservationRepository.save(reservation);
    }

    @Transactional
    public List<EmployeeReservationResponseDTO> getReservationsByEmployee(String username) {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado"));

        List<Reservation> reservations = reservationRepository.findByEmployeeId(employee.getId());
        return reservations.stream().map(this::convertToEmployeeReservationResponseDTO).collect(Collectors.toList());
    }

    private EmployeeReservationResponseDTO convertToEmployeeReservationResponseDTO(Reservation reservation) {
        EmployeeReservationResponseDTO dto = new EmployeeReservationResponseDTO();
        dto.setId(reservation.getId());
        dto.setDate(reservation.getDate());
        dto.setStartTime(reservation.getStartTime());
        dto.setEndTime(reservation.getEndTime());
        dto.setStatus(reservation.getStatus());
        dto.setResourceName(reservation.getResource().getName());
        dto.setStudentName(reservation.getStudent().getFirstName() + " " + reservation.getStudent().getFirstLastName());
        return dto;
    }

    @Transactional
    public String getMostLoanedResourceByTypeAndDateRange(String startDateStr, String endDateStr,
            String resourceType) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);

        List<Reservation> loans = reservationRepository.findByResourceTypeAndDateBetweenAndStatus(resourceType, startDate,
                endDate, "PRESTADO");
        if (loans.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron préstamos");
        }

        return loans.stream()
                .collect(Collectors.groupingBy(Reservation::getResource, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey()
                .getName();
    }
}
