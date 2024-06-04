package com.main.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.main.dtos.FeatureDTO;
import com.main.dtos.ResourceTypeDto;
import com.main.dtos.ResourceTypeResponseDTO;
import com.main.exceptions.ResourceNotFoundException;
import com.main.models.Feature;
import com.main.models.ServiceUnit;
import com.main.models.TypeResource;
import com.main.repositories.EmployeeRepository;
import com.main.repositories.FeatureRepository;
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
    private final FeatureRepository featureRepository;

    @Transactional
    public TypeResource createTypeResource(ResourceTypeDto dto, ServiceUnit unit) {
        TypeResource typeResource = TypeResource.builder()
                .name(dto.getName())
                .serviceUnit(unit)
                .build();

        final TypeResource savedTypeResource = typeResourceRepository.save(typeResource);

        Set<Feature> features = dto.getFeatures().stream()
                .map(featureDto -> {
                    DataType dataType;
                    try {
                        dataType = DataType.valueOf(featureDto.getType().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new ResourceNotFoundException("Tipo de dato no válido: " + featureDto.getType());
                    }
                    Feature feature = Feature.builder()
                            .name(featureDto.getName())
                            .type(dataType)
                            .typeResource(savedTypeResource)
                            .build();
                    return featureRepository.save(feature);
                })
                .collect(Collectors.toSet());

        savedTypeResource.setFeatures(features);
        return typeResourceRepository.save(savedTypeResource);
    }

    @Transactional
    public List<ResourceTypeResponseDTO> getResourceTypesByServiceUnit(String username, String rol) {
        Long serviceUnitId = null;
        ServiceUnit serviceUnit;
        if ("UNIT".equals(rol)) {
            serviceUnit = serviceUnitRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No se encontró la unidad de servicio para el usuario proporcionado"));
            serviceUnitId = serviceUnit.getId();
        } else if ("EMPLOYEE".equals(rol)) {
            Employee employee = employeeRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException(
                            "No se encontró el empleado para el usuario proporcionado"));
            serviceUnit = employee.getServiceUnit();
            serviceUnitId = serviceUnit.getId();
        } else {
            throw new ResourceNotFoundException("Rol no válido");
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

    @Transactional
    public List<FeatureDTO> findResourceTypeFeaturesByName(String username, String name) {
        ServiceUnit serviceUnit = serviceUnitRepository.findByEmployeeUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la unidad de servicio para el empleado proporcionado"));

        TypeResource typeResource = typeResourceRepository.findByNameAndServiceUnitId(name, serviceUnit.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el tipo de recurso con el nombre proporcionado en la unidad de servicio"));

        return featureRepository.findByTypeResourceIdWithResourceFeatures(typeResource.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<FeatureDTO> findResourceTypeFeaturesByUnitAndName(String unitServiceName, String resourceName) {
        ServiceUnit serviceUnit = serviceUnitRepository.findByUsername(unitServiceName)
                .orElseThrow(
                        () -> new ResourceNotFoundException("No se encontró la unidad de servicio: " + unitServiceName));

        TypeResource typeResource = typeResourceRepository.findByNameAndServiceUnitId(resourceName, serviceUnit.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el tipo de recurso con el nombre proporcionado en la unidad de servicio"));

        return featureRepository.findByTypeResourceIdWithResourceFeatures(typeResource.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private FeatureDTO convertToDto(Feature feature) {
        FeatureDTO dto = new FeatureDTO();
        dto.setId(feature.getId());
        dto.setName(feature.getName());
        dto.setType(feature.getType().name());

        // Obtener el valor de resourceFeatures
        if (feature.getResourceFeatures() != null && !feature.getResourceFeatures().isEmpty()) {
            dto.setValue(
                    feature.getResourceFeatures().iterator().next().getValue()
            );
        } else {
            dto.setValue(null);
        }

        return dto;
    }
}
