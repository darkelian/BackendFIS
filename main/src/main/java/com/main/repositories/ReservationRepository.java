package com.main.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.main.models.Reservation;
import com.main.models.Resource;
import com.main.models.Student;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByResourceAndDate(Resource resource, LocalDate date);

    List<Reservation> findByStudentAndStatus(Student student, String status);

    List<Reservation> findByStudent(Student student);

    @Query("SELECT r FROM Reservation r WHERE r.resource.type.name = :resourceType AND r.date BETWEEN :startDate AND :endDate")
    List<Reservation> findByResourceTypeAndDateBetween(@Param("resourceType") String resourceType,
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Nueva consulta para buscar reservaciones por empleado
    List<Reservation> findByEmployeeId(Long employeeId);

    @Query("SELECT r FROM Reservation r WHERE r.resource.type.name = :resourceType AND r.date BETWEEN :startDate AND :endDate AND r.status = :status")
    List<Reservation> findByResourceTypeAndDateBetweenAndStatus(
            @Param("resourceType") String resourceType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") String status);
}
