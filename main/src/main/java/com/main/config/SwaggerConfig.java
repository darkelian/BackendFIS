package com.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;

@Configuration
public class SwaggerConfig {
    String schemeName = "bearerAuth";
    String bearerFormat = "JWT";
    String scheme = "bearer";

    @Bean
    public OpenAPI caseOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList(schemeName))
                .components(new Components()
                        .addSecuritySchemes(
                                schemeName, new SecurityScheme()
                                        .name(schemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .bearerFormat(bearerFormat)
                                        .in(SecurityScheme.In.HEADER)
                                        .scheme(scheme)))
                .info(new Info()
                        .title("Servicios Backend de Integraservicios")
                        .description("Documentación detallada de los servicios y esquemas para los modulos de Integraservicios")
                        .version("1.8.9"))
                .addTagsItem(new Tag().name("Administrador").description("Operaciones del modulo de administrador"))
                .addTagsItem(new Tag().name("Unidad de Servicios").description("Operaciones del módulo unidad de servicios"))
                .addTagsItem(new Tag().name("Empleados").description("Operaciones del módulo empleados"))
                .addTagsItem(new Tag().name("Estudiantes").description("Operaciones del módulo de estudiantes"))
                .addTagsItem(new Tag().name("Autenticación").description("Operaciones de autenticación"));
    }
}