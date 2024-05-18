package com.main.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final String SERVICE_UNIT_NOT_FOUND = "Unidad de servicio no encontrada";
    private static final String SCHEDULE_ALREADY_EXISTS = "Ya existe un horario para la fecha proporcionada en la unidad de servicio especificada";

    @Transactional
    public void createSchedule(ServiceUnitAvailabilityDTO scheduleDTO, String username) {
        LocalDate date = parseDate(scheduleDTO.getAvailability().get(0).getDate());

        ServiceUnit serviceUnit = findServiceUnitByUsername(username);

        validateScheduleNotExists(date, serviceUnit);

        AvailabilitySchedule schedule = buildAvailabilitySchedule(scheduleDTO, date, serviceUnit);

        scheduleRepository.save(schedule);
    }

    @Transactional(readOnly = true)
    public ServiceUnitAvailabilityDTO getScheduleByServiceUnitName(String username) {
        ServiceUnit serviceUnit = findServiceUnitByUsername(username);

        List<AvailabilitySchedule> schedules = scheduleRepository.findByServiceUnit(serviceUnit);

        List<DayAvailabilityDTO> dayAvailabilityList = buildDayAvailabilityList(schedules);

        return new ServiceUnitAvailabilityDTO(dayAvailabilityList);
    }

    private LocalDate parseDate(String dateString) {
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }

    private ServiceUnit findServiceUnitByUsername(String username) {
        return serviceUnitRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(SERVICE_UNIT_NOT_FOUND));
    }

    private void validateScheduleNotExists(LocalDate date, ServiceUnit serviceUnit) {
        List<AvailabilitySchedule> existingSchedules = scheduleRepository.findByDateAvailabilityAndServiceUnit(date, serviceUnit);
        if (!existingSchedules.isEmpty()) {
            throw new DataIntegrityViolationException(SCHEDULE_ALREADY_EXISTS);
        }
    }

    private AvailabilitySchedule buildAvailabilitySchedule(ServiceUnitAvailabilityDTO scheduleDTO, LocalDate date, ServiceUnit serviceUnit) {
        AvailabilitySlotDTO slot = scheduleDTO.getAvailability().get(0).getTimeSlots().get(0);
        LocalTime startTime = parseTime(slot.getStartTime());
        LocalTime endTime = parseTime(slot.getEndTime());

        AvailabilitySchedule schedule = new AvailabilitySchedule();
        schedule.setDateAvailability(date);
        schedule.setServiceUnit(serviceUnit);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);

        return schedule;
    }

    private LocalTime parseTime(String timeString) {
        return LocalTime.parse(timeString, TIME_FORMATTER);
    }

    private List<DayAvailabilityDTO> buildDayAvailabilityList(List<AvailabilitySchedule> schedules) {
        Map<String, List<AvailabilitySlotDTO>> availabilityMap = schedules.stream()
                .collect(Collectors.groupingBy(
                        schedule -> schedule.getDateAvailability().toString(),
                        Collectors.mapping(
                                schedule -> new AvailabilitySlotDTO(
                                        schedule.getStartTime().toString(),
                                        schedule.getEndTime().toString()),
                                Collectors.toList())));

        return availabilityMap.entrySet().stream()
                .map(entry -> new DayAvailabilityDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
