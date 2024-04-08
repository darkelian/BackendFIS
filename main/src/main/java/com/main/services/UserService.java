package com.main.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.main.dtos.AdminRequest;
import com.main.dtos.EmployeeRequest;
import com.main.models.DocumentType;
import com.main.models.Employee;
import com.main.models.Role;
import com.main.models.User;
import com.main.repositories.EmployeeRepository;
import com.main.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;

@Service
@Data
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeRepository employeeRepository;

    // Metodo para registrar un usuario administrador
    @Transactional
    public User registerAdministrator(AdminRequest request) {
        // Generar un username base.
        String baseUsername = "admin_";

        // Generar un username único a partir del base
        String uniqueUsername = generateUniqueUsername(baseUsername);

        // Crear el nuevo User con todos los detalles configurados, incluido el username
        User adminUser = new User();
        adminUser.setUsername(uniqueUsername);
        adminUser.setRole(Role.ADMINISTRATOR);
        adminUser.setPassword(passwordEncoder.encode(request.getPassword()));

        // Guardar el usuario con el username único
        return userRepository.save(adminUser);
    }

    private String generateUniqueUsername(String base) {
        String username;
        int counter = 0;

        // Empieza la búsqueda desde "admin_1", ya que "admin_" sin número no es deseado
        do {
            counter++;
            username = base + counter;
        } while (userRepository.findByUsername(username).isPresent());

        return username;
    }

    // Metodo para generar un usuario Empleado
    @Transactional
    public Employee registerEmployee(EmployeeRequest employeeRequest) {
        // Crear el usuario asociado al empleado
        User user = new User();
        user.setUsername(String.valueOf(employeeRequest.getDocument()));
        user.setPassword(passwordEncoder.encode(employeeRequest.getPassword()));
        user.setRole(Role.EMPLOYEE); // Asegúrate de que Role.EMPLOYEE existe en tu enum Role
        userRepository.save(user);

        // Convertir EmployeeRequest a la entidad Employee
        Employee employee = convertToEmployee(employeeRequest);
        employee.setUser(user);

        // Asignar el usuario creado al empleado y guardar el empleado
        return employeeRepository.save(employee);
    }

    private Employee convertToEmployee(EmployeeRequest employeeRequest) {
        Employee employee = new Employee();
        employee.setDocumentType(DocumentType.valueOf(employeeRequest.getDocumentType().toUpperCase()));
        employee.setDocument(employeeRequest.getDocument());
        employee.setEmail(employeeRequest.getEmail());
        employee.setFirstName(employeeRequest.getFirstName());
        employee.setMiddleName(employeeRequest.getMiddleName());
        employee.setFirstLastName(employeeRequest.getFirstLastName());
        employee.setMiddleLastName(employeeRequest.getMiddleLastName());
        return employee;
    }
}
