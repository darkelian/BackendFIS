package com.main.services;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.main.dtos.ResourceTypeDto;
import com.main.models.Feature;
import com.main.models.ServiceUnit;
import com.main.models.TypeResource;
import com.main.repositories.TypeResourceRepository;
import com.main.models.DataType;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TypeResourceService {
    private final TypeResourceRepository typeResourceRepository;

    @Transactional
    public TypeResource createTypeResource(ResourceTypeDto dto, ServiceUnit unit) {
        TypeResource typeResource = TypeResource.builder()
                .name(dto.getName())
                .serviceUnit(unit)
                .build();

        Set<Feature> features = dto.getFeatures().stream()
                .map(featureDto -> {
                    DataType dataType;
                    try {
                        dataType = DataType.valueOf(featureDto.getType().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Tipo de dato no válido: " + featureDto.getType());
                    }
                    return Feature.builder()
                            .name(featureDto.getName())
                            .type(dataType)
                            .typeResource(typeResource)
                            .build();
                })
                .collect(Collectors.toSet());

        typeResource.setFeatures(features);
        return typeResourceRepository.save(typeResource);
    }
}
