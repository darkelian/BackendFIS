package com.main.utils;

import com.main.security.JwtService;

import lombok.Data;

@Data
public class JwtUtil {
    public static String extractUsernameFromHeader(JwtService jwtService, String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7);
            return jwtService.getUsernameFromToken(jwtToken);
        }
        return null;
    }
}