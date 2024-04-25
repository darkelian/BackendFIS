package com.main.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.main.models.AvailabilitySchedule;
import com.main.models.DayOfWeek;
import com.main.models.ServiceUnit;

@Repository
public interface AvailabilityScheduleRepository extends JpaRepository<AvailabilitySchedule, Long> {
    boolean existsByDayOfWeekAndServiceUnit(DayOfWeek dayOfWeek, ServiceUnit serviceUnit);

    List<AvailabilitySchedule> findByServiceUnit(ServiceUnit serviceUnit);
}
