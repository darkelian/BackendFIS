package com.main.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.models.Reservation;
import com.main.models.Resource;
import com.main.models.Student;

public interface ReservationRepository extends JpaRepository<Reservation, Long>{
    List<Reservation> findByResourceAndDate(Resource resource, LocalDate date);

    List<Reservation> findByStudent(Student student);
}
