package com.main.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.main.dtos.AuthResponse;
import com.main.dtos.LoginRequest;
import com.main.repositories.UserRepository;
import com.main.security.JwtService;
import com.main.utils.RoleUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;

        public AuthResponse login(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
                UserDetails user = userRepository.findByUsername(request.getUsername()).orElseThrow();
                String token = jwtService.getToken(user);
                String primaryRole = RoleUtil.getPrimaryRole(user);
                return AuthResponse.builder()
                                .token(token)
                                .rol(primaryRole)
                                .build();
        }
}
