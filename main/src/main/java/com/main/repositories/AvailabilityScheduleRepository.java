package com.main.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.main.models.AvailabilitySchedule;
import com.main.models.ServiceUnit;

@Repository
public interface AvailabilityScheduleRepository extends JpaRepository<AvailabilitySchedule, Long> {
    List<AvailabilitySchedule> findByDateAvailabilityAndServiceUnit(Date date, ServiceUnit serviceUnit);
    List<AvailabilitySchedule> findByServiceUnit(ServiceUnit serviceUnit);
}
