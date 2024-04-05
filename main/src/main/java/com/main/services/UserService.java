package com.main.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.main.models.Role;
import com.main.models.User;
import com.main.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;

@Service
@Data
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerAdministrator(String password) {
        // Generar un username base.
        String baseUsername = "admin_";

        // Generar un username único a partir del base
        String uniqueUsername = generateUniqueUsername(baseUsername);

        // Crear el nuevo User con todos los detalles configurados, incluido el username
        User adminUser = new User();
        adminUser.setUsername(uniqueUsername);
        adminUser.setRole(Role.ADMINISTRATOR);
        adminUser.setPassword(passwordEncoder.encode(password));

        // Guardar el usuario con el username único
        return userRepository.save(adminUser);
    }

    private String generateUniqueUsername(String base) {
        String username;
        int counter = 0;

        // Empieza la búsqueda desde "admin_1", ya que "admin_" sin número no es deseado
        do {
            counter++;
            username = base + counter;
        } while (userRepository.findByUsername(username).isPresent());

        return username;
    }

}
