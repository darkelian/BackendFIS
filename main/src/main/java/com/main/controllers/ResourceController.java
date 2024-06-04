package com.main.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.main.dtos.AvailableResourceDTO;
import com.main.dtos.EmployeeReservationResponseDTO;
import com.main.dtos.FeatureDTO;
import com.main.dtos.MostReservedRequestDTO;
import com.main.dtos.ReservationRequestDTO;
import com.main.dtos.ResourceCreationDTO;
import com.main.dtos.ResourceTypeDto;
import com.main.dtos.ResourceTypeResponseDTO;
import com.main.dtos.StandardResponseDTO;
import com.main.models.ServiceUnit;
import com.main.repositories.UserRepository;
import com.main.security.JwtService;
import com.main.services.ReservationService;
import com.main.services.ResourceService;
import com.main.services.ScheduleService;
import com.main.services.TypeResourceService;
import com.main.services.UnitService;
import com.main.utils.JwtUtil;
import com.main.utils.RoleUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Data
@RestController
@RequestMapping("api/resources")
@AllArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;
    private final ScheduleService scheduleService;
    private final JwtService jwtService;
    private final TypeResourceService typeResourceService;
    private final UnitService unitService;
    private final UserRepository userRepository;
    private final ReservationService reservationService;

    @PostMapping("/type")
    @Tag(name = "Unidad de Servicios")
    @Operation(summary = "Crear un nuevo tipo de recurso")
    public ResponseEntity<StandardResponseDTO> createTypeResource(
            @RequestHeader("Authorization") String authorizationHeader,
            @Validated @RequestBody ResourceTypeDto request) {

        String username = JwtUtil.extractUsernameFromHeader(jwtService, authorizationHeader);

        if (scheduleService.getScheduleByServiceUnitName(username).getAvailability().isEmpty()) {
            throw new DataIntegrityViolationException(
                    "Sin disponibilidad horaria, agregar una disponibilidad horaria primero");
        }

        ServiceUnit serviceUnit = unitService.getServicesUnitByUsername(username)
                .orElseThrow(() -> new IllegalStateException(
                        "No se encontró la unidad de servicio para el usuario proporcionado"));

        typeResourceService.createTypeResource(request, serviceUnit);

        StandardResponseDTO response = new StandardResponseDTO().fullSuccess("Tipo de recurso creado con éxito");
        return ResponseEntity.ok(response);
    }

    // Crear recursos
    @PostMapping("/create")
    @Tag(name = "Empleados")
    @Operation(summary = "Crear un nuevo recurso")
    public ResponseEntity<StandardResponseDTO> createResource(@Validated @RequestBody ResourceCreationDTO resourceDTO) {
        resourceService.createResource(resourceDTO);
        StandardResponseDTO response = new StandardResponseDTO().fullSuccess("Recurso creado exitosamente");
        return ResponseEntity.ok(response);
    }

    // Consultar los tipos de recurso de una unidad de servicio
    @GetMapping("/types")
    @Tag(name = "Unidad de Servicios")
    @Operation(summary = "Obtener tipos de recursos por unidad de servicio")
    public ResponseEntity<StandardResponseDTO> getResourceTypesByServiceUnit(
            @RequestHeader("Authorization") String authorizationHeader) {
        String username = JwtUtil.extractUsernameFromHeader(jwtService, authorizationHeader);
        String rol = RoleUtil.getPrimaryRole(userRepository, username);
        List<ResourceTypeResponseDTO> resourceTypes = typeResourceService.getResourceTypesByServiceUnit(username, rol);
        return ResponseEntity.ok(
                resourceTypes.isEmpty() ? new StandardResponseDTO().failSuccess("No se encontraron tipos de recursos")
                        : new StandardResponseDTO().fullSuccess(resourceTypes));
    }

    // Consultar las características de un tipo de recurso
    @GetMapping("/types/features")
    @Tag(name = "Empleados")
    @Operation(summary = "Buscar tipos de recursos por nombre para empleados y obtener sus características")
    public ResponseEntity<StandardResponseDTO> findResourceTypesWithFeaturesByName(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String name) {
        String username = JwtUtil.extractUsernameFromHeader(jwtService, authorizationHeader);
        List<FeatureDTO> features = typeResourceService.findResourceTypeFeaturesByName(username, name);

        StandardResponseDTO response = new StandardResponseDTO().fullSuccess(features.isEmpty()
                ? "No se encontraron características para el tipo de recurso"
                : features);
        return ResponseEntity.ok(response);
    }

    // Consultar los recursos con estado de disponibles
    @GetMapping("/available")
    @Tag(name = "Estudiantes")
    @Operation(summary = "Obtener recursos disponibles")
    public ResponseEntity<StandardResponseDTO> getAvailableResources() {
        List<AvailableResourceDTO> availableResources = resourceService.getAvailableResources();
        StandardResponseDTO response = new StandardResponseDTO();
        response.setSuccess(true);
        response.setData(availableResources);
        response.setCount(availableResources.size());
        return ResponseEntity.ok(response);
    }

    // Reservar un recurso
    @PostMapping("/reserve")
    @Tag(name = "Estudiantes")
    @Operation(summary = "Reservar un recurso")
    public ResponseEntity<StandardResponseDTO> reserveResource(
            @RequestHeader("Authorization") String authorizationHeader,
            @Validated @RequestBody ReservationRequestDTO request) {
        String username = JwtUtil.extractUsernameFromHeader(jwtService, authorizationHeader);
        String reserve = reservationService.createReservation(request, username);
        StandardResponseDTO response = new StandardResponseDTO()
                .fullSuccess("Recurso " + reserve + " reservado exitosamente");
        return ResponseEntity.ok(response);
    }

    // Recurso más reservado en el sistema
    @GetMapping("/most/reserved")
    @Tag(name = "Empleados")
    @Operation(summary = "Obtener el recurso más reservado en el sistema")
    public ResponseEntity<StandardResponseDTO> getMostReservedResource() {
        String mostReservedResource = reservationService.getMostReservedResource();
        StandardResponseDTO response = new StandardResponseDTO().fullSuccess("El recurso más reservado es: "
                + mostReservedResource);
        return ResponseEntity.ok(response);
    }

    // Recurso más reservado por un tipo de recurso y rango de fechas
    @GetMapping("/most/reserved/type")
    @Tag(name = "Administrador")
    @Operation(summary = "Obtener el recurso más reservado por un tipo de recurso y rango de fechas")
    public ResponseEntity<StandardResponseDTO> getMostReservedResourceByTypeAndDateRange(
            @RequestBody MostReservedRequestDTO request) {
        String mostReservedResource = reservationService.getMostReservedResourceByTypeAndDateRange(
                request.getStartDate(), request.getEndDate(),
                request.getResourceName());
        StandardResponseDTO response = new StandardResponseDTO().fullSuccess("El recurso más reservado es: "
                + mostReservedResource);
        return ResponseEntity.ok(response);
    }

    // Volver una reserva a un prestamo
    @PutMapping("/borrowed")
    @Tag(name = "Empleados")
    @Operation(summary = "Cambiar el estado de una reserva a prestamo")
    public ResponseEntity<StandardResponseDTO> changeReservationToBorrowed(
            @RequestParam Long reservationId) {
        reservationService.changeReservationToLoan(reservationId);
        StandardResponseDTO response = new StandardResponseDTO().fullSuccess("Reserva cambiada a prestamo");
        return ResponseEntity.ok(response);
    }

    // Volver una reserva a disponible
    @PutMapping("/available")
    @Tag(name = "Empleados")
    @Operation(summary = "Cambiar el estado de una reserva a disponible")
    public ResponseEntity<StandardResponseDTO> changeReservationToAvailable(
            @RequestParam Long reservationId) {
        reservationService.changeLoanToReturned(reservationId);
        StandardResponseDTO response = new StandardResponseDTO().fullSuccess("Reserva cambiada a disponible");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee")
    @Tag(name = "Empleados")
    @Operation(summary = "Obtener las reservaciones asignadas a un empleado")
    public ResponseEntity<StandardResponseDTO> getReservationsByEmployee(
            @RequestHeader("Authorization") String authorizationHeader) {
        String username = JwtUtil.extractUsernameFromHeader(jwtService, authorizationHeader);
        List<EmployeeReservationResponseDTO> reservations = reservationService.getReservationsByEmployee(username);
        return ResponseEntity.ok(new StandardResponseDTO().fullSuccess(reservations));
    }

    // Consultar los recursos más prestados
    @Tag(name = "Administrador")
    @Operation(summary = "Obtener los recursos más prestados")
    @GetMapping("/most/loaned/type")
    public ResponseEntity<StandardResponseDTO> getMostLoanedResourceByTypeAndDateRange(@RequestBody MostReservedRequestDTO request) {
        String mostLoanedResource = reservationService.getMostLoanedResourceByTypeAndDateRange(
                request.getStartDate(), request.getEndDate(),
                request.getResourceName());
        StandardResponseDTO response = new StandardResponseDTO().fullSuccess("El recurso más prestado es: " + mostLoanedResource);
        return ResponseEntity.ok(response);
    }
}
