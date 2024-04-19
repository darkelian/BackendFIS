package com.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.models.AvailabilitySchedule;
import com.main.models.DayOfWeek;
import com.main.models.ServiceUnit;

public interface AvailabilityScheduleRepository extends JpaRepository<AvailabilitySchedule, Long> {
    boolean existsByDayOfWeekAndServiceUnit(DayOfWeek dayOfWeek, ServiceUnit serviceUnit);
}
