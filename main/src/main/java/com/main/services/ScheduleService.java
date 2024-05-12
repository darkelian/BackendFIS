package com.main.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.text.ParseException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.main.dtos.AvailabilitySlotDTO;
import com.main.dtos.DayAvailabilityDTO;
import com.main.dtos.ServiceUnitAvailabilityDTO;
import com.main.models.AvailabilitySchedule;
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
        // Convertir la cadena de la fecha en un objeto Date
        String dateString = scheduleDTO.getAvailability().get(0).getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            throw new DataIntegrityViolationException("La fecha debe estar en el formato MM/dd/yyyy");
        }
        // Buscar la entidad ServiceUnit por el nombre de usuario
        ServiceUnit serviceUnit = serviceUnitRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Unidad de servicio no encontrada"));

        // Verificar si ya existe un horario para la fecha y unidad de servicio dada
        List<AvailabilitySchedule> existingSchedules = scheduleRepository.findByDateAvailabilityAndServiceUnit(date,
                serviceUnit);
        if (!existingSchedules.isEmpty()) {
            throw new DataIntegrityViolationException(
                    "Ya existe un horario para la fecha proporcionada en la unidad de servicio especificada");
        }

        // Crear un nuevo horario de disponibilidad
        AvailabilitySchedule schedule = new AvailabilitySchedule();
        schedule.setDateAvailability(date);
        schedule.setServiceUnit(serviceUnit);
        schedule.setStartTime(scheduleDTO.getAvailability().get(0).getTimeSlots().get(0).getStartTime());
        schedule.setEndTime(scheduleDTO.getAvailability().get(0).getTimeSlots().get(0).getEndTime());

        // Guardar el horario de disponibilidad en la base de datos
        scheduleRepository.save(schedule);
    }

    @Transactional(readOnly = true)
    public ServiceUnitAvailabilityDTO getScheduleByServiceUnitName(String username) {
        // Buscar la entidad ServiceUnit por el nombre de usuario
        ServiceUnit serviceUnit = serviceUnitRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Unidad de servicio no encontrada"));

        // Buscar todos los horarios asociados a la unidad de servicio
        List<AvailabilitySchedule> schedules = scheduleRepository.findByServiceUnit(serviceUnit);

        // Agrupar los horarios por fecha de disponibilidad
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Map<String, List<AvailabilitySlotDTO>> availabilityMap = schedules.stream()
                .collect(Collectors.groupingBy(
                        schedule -> sdf.format(schedule.getDateAvailability()),
                        Collectors.mapping(
                                schedule -> new AvailabilitySlotDTO(schedule.getStartTime(), schedule.getEndTime()),
                                Collectors.toList())));

        // Convertir el mapa a una lista de DayAvailabilityDTO
        List<DayAvailabilityDTO> dayAvailabilityList = availabilityMap.entrySet().stream()
                .map(entry -> new DayAvailabilityDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        // Crear y retornar el ServiceUnitAvailabilityDTO
        return new ServiceUnitAvailabilityDTO(dayAvailabilityList);
    }
}
