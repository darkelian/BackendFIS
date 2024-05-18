package com.main.dtos;

import java.util.List;

import lombok.Data;

@Data
public class ResourceCreationDTO {
    private String name;
    private Long typeId;
    private List<FeatureDTO> features;
}