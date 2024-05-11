package com.main.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceFeatureDto {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;
    @NotBlank(message = "El tipo no puede estar vacío")
    private String type;
}
