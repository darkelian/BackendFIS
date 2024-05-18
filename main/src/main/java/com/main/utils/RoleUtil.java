package com.main.utils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.main.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

public class RoleUtil {

    public static String getPrimaryRole(UserDetails user) {
        return user.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("No Role Assigned");
    }

    public static List<String> getAllRoles(UserDetails user) {
        return user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    public static String getPrimaryRole(UserRepository userRepository, String username) {
        UserDetails user = userRepository.findByUsername(username).orElseThrow();
        return user.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("No Role Assigned");
    }
}