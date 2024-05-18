package com.main.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.main.dtos.ResourceCreationDTO;
import com.main.models.Feature;
import com.main.models.Resource;
import com.main.models.ResourceFeatureId;
import com.main.models.ResourceFeatures;
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

        Resource resource = new Resource();
        resource.setName(resourceDTO.getName());
        resource.setType(typeResource);

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
}
