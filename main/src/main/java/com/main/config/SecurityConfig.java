package com.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.main.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final AuthenticationProvider authProvider;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .csrf(csrf -> csrf
                                                .disable())
                                .cors(cors -> {
                                })
                                .authorizeHttpRequests(authRequest -> authRequest
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers("/api/register/**").hasAuthority("ADMINISTRATOR")
                                                .requestMatchers("/api/admin/**").hasAuthority("ADMINISTRATOR")
                                                .requestMatchers("/api/unit/**").hasAuthority("UNIT")
                                                .requestMatchers(HttpMethod.POST, "/api/resources/type")
                                                .hasAuthority("UNIT")
                                                .requestMatchers(HttpMethod.GET, "/api/resources/type")
                                                .hasAnyAuthority("UNIT", "EMPLOYEE")
                                                .requestMatchers(HttpMethod.GET, "/api/resources/available").hasAuthority("STUDENT")
                                                .requestMatchers("api/student/**").hasAuthority("STUDENT")
                                                .requestMatchers("/api/resources/create").hasAuthority("EMPLOYEE")
                                                .requestMatchers("/api/resources/reserve").hasAuthority("STUDENT")
                                                .requestMatchers("/api/resources/most/reserved/type").hasAuthority("ADMINISTRATOR")
                                                .requestMatchers("/api/resources/most/loaned/type").hasAuthority("ADMINISTRATOR")
                                                .requestMatchers("/api/resources/borrowed").hasAuthority("EMPLOYEE")
                                                .requestMatchers( "/api/resources/employee").hasAuthority("EMPLOYEE")
                                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**",
                                                                "/swagger-ui.html", "**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .sessionManagement(sessionManager -> sessionManager
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authProvider)
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .exceptionHandling(exceptionHandling -> exceptionHandling
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        if (authException instanceof AuthenticationServiceException) {
                                                                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                                                response.getWriter().write("Unauthorized: "
                                                                                + authException.getMessage());
                                                        }
                                                }))
                                .build();
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.addAllowedOrigin("http://localhost:4200");
                configuration.addAllowedOrigin("https://integraservicios.onrender.com/");
                configuration.addAllowedMethod("*");
                configuration.addAllowedHeader("*");
                configuration.setAllowCredentials(true);
                configuration.addAllowedMethod(HttpMethod.OPTIONS);
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
