package com.main.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import java.util.List;

import com.main.dtos.AdminRequest;
import com.main.services.UserService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class DataLoader implements CommandLineRunner {
    private final UserService userService; // Asumiendo que este es tu servicio con el método registerAdministrator
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    @Override
    public void run(String... args) throws Exception {
        if (isDatabaseEmpy()) {
            Resource resource = resourceLoader.getResource("classpath:data/administrators.json");
            List<AdminPassword> adminPasswords = objectMapper.readValue(resource.getInputStream(),
                    new TypeReference<List<AdminPassword>>() {
                    });
            adminPasswords.forEach(admin -> {
                AdminRequest request = new AdminRequest();
                request.setPassword(admin.getPassword());
                userService.registerAdministrator(request);
            });
        }
    }

    // Modelos para la carga de datos
    static class AdminPassword {
        private String password;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    private boolean isDatabaseEmpy() {
        return userService.getUserRepository().count() == 0;
    }
}
