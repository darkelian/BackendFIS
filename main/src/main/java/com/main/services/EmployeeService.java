package com.main.services;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.main.dtos.EmployeeResponse;
import com.main.models.Employee;
import com.main.models.ServiceUnit;
import com.main.repositories.EmployeeRepository;
import com.main.repositories.ServiceUnitRepository;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Service
@AllArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ServiceUnitRepository serviceUnityRepository;

    // Traer todos los empleados
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(EmployeeResponse::new)
                .collect(Collectors.toList());
    }

    // Traer Empleados por unidad de servicio
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getByServiceUnitEmployees(String unitName, boolean includeUnassigned) {
        List<Employee> employees;
        if (includeUnassigned) {
            employees = employeeRepository.findUnassignedEmployees();
        } else if (unitName != null && !unitName.trim().isEmpty()) {
            employees = employeeRepository.findByServiceUnitName(unitName);
        } else {
            // Caso donde el nombre de la unidad es null o vacío, y no queremos incluir no
            // asignados
            employees = Collections.emptyList();
        }
        return employees.stream()
                .map(EmployeeResponse::new)
                .collect(Collectors.toList());
    }

    // Asingnar la unidad de servicio
    @Transactional
    public EmployeeResponse assignServiceUnitToEmployee(Long employeeId, String unitName) {
        ServiceUnit serviceUnit = serviceUnityRepository.findByUsername(unitName)
                .orElseThrow(() -> new  DataIntegrityViolationException(
                        "Unidad de servicio con nombre '" + unitName + "' no encontrada"));

        Employee employee = employeeRepository.findByDocument(employeeId)
                .orElseThrow(() -> new  DataIntegrityViolationException("Empleado con Documento'" + employeeId + "' no encontrado"));

        employee.setServiceUnit(serviceUnit); // Asume que Employee tiene un campo 'serviceUnit' para esta relación
        Employee updatedEmployee = employeeRepository.save(employee);

        return new EmployeeResponse(updatedEmployee);
    }
}