package com.main.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceUnitRequest {

    @NotNull(message = "El tiempo de granularidad en minutos es obligatorio.")
    @Min(value = 15,message = "El  tiempo no es valido debe ser minimamente de 15 minutos.")
    private int granularityInMinutes;

    @NotBlank(message = "El nombre es obligatorio")
    private String username;
    
    @NotBlank(message = "La contraseña no puede estar vacía.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    private String password;
}
