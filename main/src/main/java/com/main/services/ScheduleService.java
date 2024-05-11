package com.main.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.main.dtos.AvailabilitySlotDTO;
import com.main.dtos.DayAvailabilityDTO;
import com.main.dtos.ServiceUnitAvailabilityDTO;
import com.main.models.AvailabilitySchedule;
import com.main.models.DayOfWeek;
import com.main.models.ServiceUnit;
import com.main.repositories.AvailabilityScheduleRepository;
import com.main.repositories.ServiceUnityRepository;

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

    // Consultar el horario de una unidad de servicio
    @Transactional(readOnly = true)
    public ServiceUnitAvailabilityDTO getScheduleByServiceUnitName(String username) {
        // Buscar la entidad ServiceUnit por el nombre
        ServiceUnit serviceUnit = serviceUnitRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Unidad de servicio no encontrada"));

        // Buscar todos los horarios asociados a la unidad de servicio
        List<AvailabilitySchedule> schedules = scheduleRepository.findByServiceUnit(serviceUnit);

        // Agrupar los horarios por DayOfWeek
        Map<DayOfWeek, List<AvailabilitySlotDTO>> availabilityMap = schedules.stream()
                .collect(Collectors.groupingBy(
                        AvailabilitySchedule::getDayOfWeek,
                        Collectors.mapping(schedule -> new AvailabilitySlotDTO(
                                schedule.getStartTime(),
                                schedule.getEndTime()), Collectors.toList())));

        // Convertir el mapa a una lista de DayAvailabilityDTO
        List<DayAvailabilityDTO> dayAvailabilityList = availabilityMap.entrySet().stream()
                .map(entry -> new DayAvailabilityDTO(entry.getKey().toString(), entry.getValue()))
                .collect(Collectors.toList());

        // Crear y retornar el ServiceUnitAvailabilityDTO
        return new ServiceUnitAvailabilityDTO(dayAvailabilityList);
    }

}
