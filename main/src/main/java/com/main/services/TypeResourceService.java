package com.main.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.main.dtos.ResourceTypeDto;
import com.main.dtos.ResourceTypeResponseDTO;
import com.main.models.Feature;
import com.main.models.ServiceUnit;
import com.main.models.TypeResource;
import com.main.repositories.EmployeeRepository;
import com.main.repositories.ServiceUnitRepository;
import com.main.repositories.TypeResourceRepository;
import com.main.models.DataType;
import com.main.models.Employee;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TypeResourceService {
    private final TypeResourceRepository typeResourceRepository;
    private final ServiceUnitRepository serviceUnitRepository;
    private final EmployeeRepository employeeRepository;

    // Crear un nuevo tipo de recurso
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

    // Obtener el tipo de recurso de una unidad de servicio
    @Transactional
    public List<ResourceTypeResponseDTO> getResourceTypesByServiceUnit(String username, String rol) {
        Long serviceUnitId = null;
        ServiceUnit serviceUnit;
        if ("UNIT".equals(rol)) {
            serviceUnit = serviceUnitRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException(
                            "No se encontró la unidad de servicio para el usuario proporcionado"));
            serviceUnitId = serviceUnit.getId();
        } else if ("EMPLOYEE".equals(rol)) {
            Employee employee = employeeRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException(
                            "No se encontró el empleado para el usuario proporcionado"));
            serviceUnit = employee.getServiceUnit();
            serviceUnitId = serviceUnit.getId();
        } else {
            throw new IllegalArgumentException("Rol no válido");
        }

        List<TypeResource> typeResources = typeResourceRepository.findByServiceUnitId(serviceUnitId);

        return typeResources.stream().map(typeResource -> {
            ResourceTypeResponseDTO dto = new ResourceTypeResponseDTO();
            dto.setId(typeResource.getId());
            dto.setName(typeResource.getName());
            dto.setServiceUnitName(typeResource.getServiceUnit().getName());
            return dto;
        }).collect(Collectors.toList());
    }
}
