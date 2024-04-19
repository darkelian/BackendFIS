package com.main.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilitySlotDTO {
    @NotBlank(message = "El tiempo de inicio es obligatorio.")
    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "El tiempo de inicio debe estar en formato HH:mm.")
    private String startTime;

    @NotBlank(message = "El tiempo de fin es obligatorio.")
    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "El tiempo de fin debe estar en formato HH:mm.")
    private String endTime;
}