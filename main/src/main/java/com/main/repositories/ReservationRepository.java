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

    List<Reservation> findByStudent(Student student);


    // Consultar las reservas de un estudiante con un estado específico
    List<Reservation> findByStudentAndStatus(Student student, String status);

    // Consultar las reservas de un recurso de un tipo específico en un rango de
    // fechas
    @Query("SELECT r FROM Reservation r WHERE r.resource.type.name = :resourceType AND r.date BETWEEN :startDate AND :endDate")
    List<Reservation> findByResourceTypeAndDateBetween(@Param("resourceType") String resourceType,
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
