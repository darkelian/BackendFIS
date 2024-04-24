package com.main.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.main.dtos.ServicesUnitResponse;
import com.main.repositories.ServiceUnityRepository;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Service
@AllArgsConstructor
public class UnitService {
    private final ServiceUnityRepository serviceUnityRepository;

    @Transactional(readOnly = true)
    public List<ServicesUnitResponse> getAllServicesUnit() {
        return serviceUnityRepository.findAll().stream()
                .map(serviceUnit -> {
                    return new ServicesUnitResponse(
                            serviceUnit.getGranularityInMinutes(),
                            serviceUnit.getUser().getUsername());
                })
                .collect(Collectors.toList());
    }
}
