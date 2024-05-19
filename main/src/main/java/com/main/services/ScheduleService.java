package com.main.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.main.dtos.AvailabilitySlotDTO;
import com.main.dtos.DayAvailabilityDTO;
import com.main.dtos.ServiceUnitAvailabilityDTO;
import com.main.exceptions.ResourceNotFoundException;
import com.main.models.AvailabilitySchedule;
import com.main.models.ServiceUnit;
import com.main.repositories.AvailabilityScheduleRepository;
import com.main.repositories.ServiceUnitRepository;

import lombok.AllArgsConstructor;
import lombok.Data;

@Service
@Data
@AllArgsConstructor
public class ScheduleService {

    private final AvailabilityScheduleRepository scheduleRepository;
    private final ServiceUnitRepository serviceUnitRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final String SERVICE_UNIT_NOT_FOUND = "Unidad de servicio no encontrada";
    private static final String SCHEDULE_ALREADY_EXISTS = "Ya existe un horario para la fecha proporcionada en la unidad de servicio especificada";

    @Transactional
    public void createSchedule(ServiceUnitAvailabilityDTO availabilityDTO, String username) {
        ServiceUnit serviceUnit = serviceUnitRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException(
                        "No se encontró la unidad de servicio para el usuario proporcionado"));

        List<AvailabilitySchedule> schedules = availabilityDTO.getAvailability().stream().map(availability -> {
            LocalDate date = parseDate(availability.getDate());

            // Validar si ya existe un horario para la fecha proporcionada
            validateScheduleNotExists(date, serviceUnit);

            return availability.getTimeSlots().stream().map(slot -> {
                AvailabilitySchedule schedule = new AvailabilitySchedule();
                schedule.setDateAvailability(date);
                schedule.setStartTime(parseTime(slot.getStartTime()));
                schedule.setEndTime(parseTime(slot.getEndTime()));
                schedule.setServiceUnit(serviceUnit);
                return schedule;
            }).collect(Collectors.toList());
        }).flatMap(List::stream).collect(Collectors.toList());

        scheduleRepository.saveAll(schedules);
    }

    private void validateScheduleNotExists(LocalDate date, ServiceUnit serviceUnit) {
        List<AvailabilitySchedule> existingSchedules = scheduleRepository.findByDateAvailabilityAndServiceUnit(date,
                serviceUnit);
        if (!existingSchedules.isEmpty()) {
            throw new DataIntegrityViolationException(SCHEDULE_ALREADY_EXISTS);
        }
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use dd/MM/yyyy");
        }
    }

    private LocalTime parseTime(String time) {
        return LocalTime.parse(time, TIME_FORMATTER);
    }

    @Transactional(readOnly = true)
    public ServiceUnitAvailabilityDTO getScheduleByServiceUnitName(String username) {
        ServiceUnit serviceUnit = findServiceUnitByUsername(username);

        List<AvailabilitySchedule> schedules = scheduleRepository.findByServiceUnit(serviceUnit);

        List<DayAvailabilityDTO> dayAvailabilityList = buildDayAvailabilityList(schedules);

        return new ServiceUnitAvailabilityDTO(dayAvailabilityList);
    }

    private ServiceUnit findServiceUnitByUsername(String username) {
        return serviceUnitRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(SERVICE_UNIT_NOT_FOUND));
    }

    private List<DayAvailabilityDTO> buildDayAvailabilityList(List<AvailabilitySchedule> schedules) {
        Map<String, List<AvailabilitySlotDTO>> availabilityMap = schedules.stream()
                .collect(Collectors.groupingBy(
                        schedule -> schedule.getDateAvailability().format(DATE_FORMATTER),
                        Collectors.mapping(
                                schedule -> new AvailabilitySlotDTO(
                                        schedule.getStartTime().format(TIME_FORMATTER),
                                        schedule.getEndTime().format(TIME_FORMATTER)),
                                Collectors.toList())));

        return availabilityMap.entrySet().stream()
                .map(entry -> new DayAvailabilityDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
