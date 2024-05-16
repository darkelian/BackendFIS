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
import com.main.dtos.ServiceUnitRequest;
import com.main.dtos.StudentRequest;
import com.main.services.UserService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class DataLoader implements CommandLineRunner {
    private final UserService userService;
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

    private boolean isDatabaseEmpty() {
        return userService.getUserRepository().count() == 0;
    }
}
