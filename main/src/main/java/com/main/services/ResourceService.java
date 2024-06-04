package com.main.services;

import java.util.List;
import java.util.stream.Collectors;

import java.util.Optional;
import org.springframework.stereotype.Service;

import com.main.dtos.AvailableResourceDTO;
import com.main.dtos.FeatureDTO;
import com.main.dtos.ResourceCreationDTO;
import com.main.exceptions.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException("ID de tipo de recurso no encontrado"));

        Resource resource = new Resource();
        resource.setName(resourceDTO.getName());
        resource.setType(typeResource);
        resource.setStatus(ResourceStatus.DISPONIBLE);

        // Determinar la cantidad disponible
        Optional<FeatureDTO> quantityFeature = resourceDTO.getFeatures().stream()
                .filter(featureDTO -> "Cantidad".equalsIgnoreCase(featureDTO.getName()))
                .findFirst();

        int availableQuantity = quantityFeature.map(featureDTO -> Integer.parseInt(featureDTO.getValue())).orElse(1);
        resource.setAvailableQuantity(availableQuantity);

        // Guardar el recurso primero para obtener el ID generado
        final Resource savedResource = resourceRepository.save(resource);

        List<ResourceFeatures> resourceFeatures = resourceDTO.getFeatures().stream().map(featureDTO -> {
            Feature feature = featureRepository.findById(featureDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Característica no encontrada"));

            ResourceFeatures resourceFeature = new ResourceFeatures();
            resourceFeature.setResource(savedResource);
            resourceFeature.setFeature(feature);
            resourceFeature.setValue(featureDTO.getValue());
            resourceFeature.setId(new ResourceFeatureId(savedResource.getId(), feature.getId()));
            return resourceFeature;
        }).collect(Collectors.toList());

        savedResource.setFeatures(resourceFeatures);

        // Guardar las características después de establecer los IDs de recurso y característica
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
            return dto;
        }).collect(Collectors.toList());
    }

}
