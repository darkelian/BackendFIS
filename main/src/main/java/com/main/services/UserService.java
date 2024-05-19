package com.main.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.main.dtos.AdminRequest;
import com.main.dtos.EmployeeRequest;
import com.main.dtos.ServiceUnitRequest;
import com.main.dtos.StudentRequest;
import com.main.models.DocumentType;
import com.main.models.Employee;
import com.main.models.Role;
import com.main.models.ServiceUnit;
import com.main.models.Student;
import com.main.models.User;
import com.main.repositories.EmployeeRepository;
import com.main.repositories.ServiceUnitRepository;
import com.main.repositories.StudentRepository;
import com.main.repositories.UserRepository;

import lombok.AllArgsConstructor;
import lombok.Data;

@Service
@Data
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeRepository employeeRepository;
    private final StudentRepository studentRepository;
    private final ServiceUnitRepository serviceUnityRepository;

    // Metodo generico para la creación de usuarios
    private User createUser(String username, String password, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return userRepository.save(user);
    }

    // Metodo para registrar un usuario administrador
    @Transactional
    public User registerAdministrator(AdminRequest request) {
        String uniqueUsername = generateUniqueUsername("admin_");
        return createUser(uniqueUsername, request.getPassword(), Role.ADMINISTRATOR);
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
    public Employee registerEmployee(EmployeeRequest request) {
        User user = createUser(String.valueOf(request.getDocument()), request.getPassword(), Role.EMPLOYEE);
        Employee employee = convertToEmployee(request);
        employee.setUser(user);

        // Asignar la unidad de servicio
        ServiceUnit serviceUnit = serviceUnityRepository.findById(request.getServiceUnitId())
                .orElseThrow(() -> new IllegalStateException("Unidad de servicio no encontrada"));
        employee.setServiceUnit(serviceUnit);

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

    // Registrar un estudiante
    @Transactional
    public Student registerStudent(StudentRequest request) {
        User user = createUser(String.valueOf(request.getCodeStudent()), request.getPassword(), Role.STUDENT);
        Student student = convertToStudent(request);
        student.setUser(user);
        return studentRepository.save(student);
    }

    private Student convertToStudent(StudentRequest request) {
        Student student = new Student();
        student.setDocumentType(DocumentType.valueOf(request.getDocumentType().toUpperCase()));
        student.setDocument(request.getDocument());
        student.setEmail(request.getEmail());
        student.setFirstName(request.getFirstName());
        student.setMiddleName(request.getMiddleName());
        student.setFirstLastName(request.getFirstLastName());
        student.setMiddleLastName(request.getMiddleLastName());
        student.setCodeStudent(request.getCodeStudent());
        student.setDegreeProgram(request.getDegreeProgram());
        student.setFaculty(request.getFaculty());
        return student;
    }

    // Registar una Unidad de servicio
    @Transactional
    public ServiceUnit registerServiceUnit(ServiceUnitRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.UNIT);
        userRepository.save(user);
        ServiceUnit serviceUnit = convertToUnit(request);
        serviceUnit.setUser(user);
        return serviceUnityRepository.save(serviceUnit);
    }

    public ServiceUnit convertToUnit(ServiceUnitRequest request) {
        ServiceUnit serviceUnit = new ServiceUnit();
        serviceUnit.setName(request.getUsername());
        serviceUnit.setGranularityInMinutes(request.getGranularityInMinutes());
        return serviceUnit;
    }
}
