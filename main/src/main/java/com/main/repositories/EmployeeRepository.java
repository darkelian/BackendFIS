package com.main.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.main.models.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // Traer los empleados asignados a una unidad de servicio
    @Query("SELECT e FROM Employee e WHERE e.serviceUnit.name = :unitName")
    List<Employee> findByServiceUnitName(@Param("unitName") String unitName);

    // Traer los empleados sin asignación de una unidad de servicio
    @Query("SELECT e FROM Employee e WHERE e.serviceUnit IS NULL")
    List<Employee> findUnassignedEmployees();

    Optional<Employee> findByDocument(Long document);

    // Traer un empleado por su nombre de usuario
    @Query("SELECT e FROM Employee e WHERE e.user.username = :username")
    Optional<Employee> findByUsername(@Param("username") String username);

    // Traer los empleados ordenados por la cantidad de reservas
    @Query("SELECT e FROM Employee e LEFT JOIN Reservation r ON e.id = r.employee.id GROUP BY e.id ORDER BY COUNT(r.id) ASC")
    List<Employee> findEmployeesOrderByReservationCount();
}
