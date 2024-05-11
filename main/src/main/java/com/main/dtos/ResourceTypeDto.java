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
public class ResourceTypeDto {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;
    private List<ResourceFeatureDto> features;
}