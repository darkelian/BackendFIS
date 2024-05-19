package com.main.dtos;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceTypeWithUnitDto {
    @NotBlank(message = "La unidad de servicio no puede estar vacía")
    private String unitService;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    private List<ResourceFeatureDto> features;
}