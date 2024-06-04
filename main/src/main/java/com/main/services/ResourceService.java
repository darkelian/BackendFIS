package com.main.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.main.dtos.AvailableResourceDTO;
import com.main.dtos.ResourceCreationDTO;
import com.main.models.Feature;
import com.main.models.Resource;
import com.main.models.ResourceFeatureId;
import com.main.models.ResourceFeatures;
import com.main.models.ResourceStatus;
import com.main.models.TypeResource;
import com.main.repositories.FeatureRepository;
import com.main.repositories.ResourceRepository;
import com.main.repositories.TypeResourceRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Service
@AllArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final TypeResourceRepository typeResourceRepository;
    private final FeatureRepository featureRepository;

    @Transactional
    public void createResource(ResourceCreationDTO resourceDTO) {
        TypeResource typeResource = typeResourceRepository.findById(resourceDTO.getTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid type resource ID"));

        // Verificar si hay una característica de cantidad
        int totalQuantity = resourceDTO.getFeatures().stream()
            .filter(featureDTO -> "Cantidad".equalsIgnoreCase(featureDTO.getName()))
            .mapToInt(featureDTO -> {
                try {
                    return Integer.parseInt(featureDTO.getValue());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid quantity value");
                }
            }).findFirst().orElse(1);

        Resource resource = new Resource();
        resource.setName(resourceDTO.getName());
        resource.setType(typeResource);
        resource.setStatus(ResourceStatus.DISPONIBLE);
        resource.setTotalQuantity(totalQuantity);
        resource.setAvailableQuantity(totalQuantity);

        // Save resource first to get the generated ID
        final Resource savedResource = resourceRepository.save(resource);

        List<ResourceFeatures> resourceFeatures = resourceDTO.getFeatures().stream().map(featureDTO -> {
            Feature feature = featureRepository.findById(featureDTO.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid feature ID"));

            ResourceFeatures resourceFeature = new ResourceFeatures();
            resourceFeature.setResource(savedResource);
            resourceFeature.setFeature(feature);
            resourceFeature.setValue(featureDTO.getValue());
            resourceFeature.setId(new ResourceFeatureId(savedResource.getId(), feature.getId()));
            return resourceFeature;
        }).collect(Collectors.toList());

        savedResource.setFeatures(resourceFeatures);

        // Save the features after setting the resource and feature IDs
        resourceRepository.save(savedResource);
    }

    // Obtener los recursos disponibles
    public List<AvailableResourceDTO> getAvailableResources() {
        List<Resource> availableResources = resourceRepository.findByStatus(ResourceStatus.DISPONIBLE);

        return availableResources.stream().map(resource -> {
            AvailableResourceDTO dto = new AvailableResourceDTO();
            dto.setId(resource.getId());
            dto.setName(resource.getName());
            dto.setTypeName(resource.getType().getName());
            dto.setServiceUnitName(resource.getType().getServiceUnit().getName());
            dto.setStatus(resource.getStatus().toString());
            dto.setAvailableQuantity(resource.getAvailableQuantity());
            return dto;
        }).collect(Collectors.toList());
    }

}
