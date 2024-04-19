package com.main.services;

import org.springframework.stereotype.Service;

import com.main.dtos.AvailabilitySlotDTO;
import com.main.dtos.DayAvailabilityDTO;
import com.main.dtos.ServiceUnitAvailabilityDTO;
import com.main.models.AvailabilitySchedule;
import com.main.models.DayOfWeek;
import com.main.models.ServiceUnit;
import com.main.repositories.AvailabilityScheduleRepository;
import com.main.repositories.ServiceUnityRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;

@Service
@Data
@AllArgsConstructor
public class ScheduleService {
    private final AvailabilityScheduleRepository scheduleRepository;
    private final ServiceUnityRepository serviceUnitRepository;

    @Transactional
    public void createSchedule(ServiceUnitAvailabilityDTO scheduleDTO, String username) {
        ServiceUnit serviceUnit = serviceUnitRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Unidad de servicio no encontrada"));

        for (DayAvailabilityDTO dayAvailability : scheduleDTO.getAvailability()) {
            // Convert String to DayOfWeek enum
            DayOfWeek dayOfWeek;
            try {
                dayOfWeek = DayOfWeek.valueOf(dayAvailability.getDayOfWeek().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid day of week: " + dayAvailability.getDayOfWeek());
            }

            for (AvailabilitySlotDTO slot : dayAvailability.getTimeSlots()) {
                // Verificar si ya existe un registro para ese día y esa unidad de servicio
                boolean exists = scheduleRepository.existsByDayOfWeekAndServiceUnit(dayOfWeek, serviceUnit);

                if (!exists) {
                    // Solo guardar si no existe un registro para ese día y unidad
                    AvailabilitySchedule schedule = new AvailabilitySchedule();
                    schedule.setDayOfWeek(dayOfWeek);
                    schedule.setStartTime(slot.getStartTime());
                    schedule.setEndTime(slot.getEndTime());
                    schedule.setServiceUnit(serviceUnit);
                    scheduleRepository.save(schedule);
                } else {
                    // Aquí podrías lanzar una excepción o manejar el caso como creas conveniente
                    throw new IllegalStateException(
                            "Ya existe un horario para " + dayOfWeek + " en la unidad de servicio.");
                }
            }
        }
    }
}
