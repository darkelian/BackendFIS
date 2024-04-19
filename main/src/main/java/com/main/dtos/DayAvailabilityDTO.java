package com.main.dtos;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DayAvailabilityDTO {
    @NotBlank(message = "El día de la semana es obligatorio")
    private String dayOfWeek;
    private List<AvailabilitySlotDTO> timeSlots;
}
