package com.main.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.function.Consumer;

import com.main.dtos.AdminRequest;
import com.main.dtos.EmployeeRequest;
import com.main.dtos.ResourceTypeDto;
import com.main.dtos.ResourceTypeWithUnitDto;
import com.main.dtos.ServiceUnitRequest;
import com.main.dtos.StudentRequest;
import com.main.models.ServiceUnit;
import com.main.dtos.ScheduleRequest;
import com.main.dtos.ServiceUnitAvailabilityDTO;
import com.main.services.UserService;
import com.main.services.ScheduleService;
import com.main.services.TypeResourceService;
import com.main.services.UnitService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class DataLoader implements CommandLineRunner {
    private final UserService userService;
    private final ScheduleService scheduleService;
    private final TypeResourceService typeResourceService;
    private final UnitService unitService;
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    @Override
    public void run(String... args) throws Exception {
        if (isDatabaseEmpty()) {
            loadData("classpath:data/units.json", new TypeReference<List<ServiceUnitRequest>>() {
            }, userService::registerServiceUnit);
            loadData("classpath:data/administrators.json", new TypeReference<List<AdminRequest>>() {
            }, userService::registerAdministrator);
            loadData("classpath:data/employees.json", new TypeReference<List<EmployeeRequest>>() {
            }, userService::registerEmployee);
            loadData("classpath:data/students.json", new TypeReference<List<StudentRequest>>() {
            }, userService::registerStudent);
            loadData("classpath:data/schedules.json", new TypeReference<List<ScheduleRequest>>() {
            }, this::registerSchedule);
            loadData("classpath:data/types.json", new TypeReference<List<ResourceTypeWithUnitDto>>() {
            }, this::registerResourceType);
        }
    }

    private <T> void loadData(String path, TypeReference<List<T>> typeReference, Consumer<T> consumer) {
        Resource resource = resourceLoader.getResource(path);
        try {
            List<T> dataList = objectMapper.readValue(resource.getInputStream(), typeReference);
            dataList.forEach(consumer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load data from: " + path, e);
        }
    }

    private void registerSchedule(ScheduleRequest scheduleRequest) {
        scheduleService.createSchedule(new ServiceUnitAvailabilityDTO(scheduleRequest.getAvailability()),
                scheduleRequest.getUsername());
    }

    private boolean isDatabaseEmpty() {
        return userService.getUserRepository().count() == 0;
    }

    private void registerResourceType(ResourceTypeWithUnitDto resource) {
        ServiceUnit serviceUnit = unitService.getServicesUnitByUsername(resource.getUnitService())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unidad de servicio no encontrada: " + resource.getUnitService()));
        ResourceTypeDto dto = new ResourceTypeDto(resource.getName(), resource.getFeatures());
        typeResourceService.createTypeResource(dto, serviceUnit);
    }
}
