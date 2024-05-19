package com.main.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.main.dtos.ServicesUnitResponse;
import com.main.models.ServiceUnit;
import com.main.repositories.ServiceUnitRepository;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Service
@AllArgsConstructor
public class UnitService {
    private final ServiceUnitRepository serviceUnitRepository;

    @Transactional(readOnly = true)
    public List<ServicesUnitResponse> getAllServicesUnit() {
        return serviceUnitRepository.findAll().stream()
                .map(serviceUnit -> {
                    return new ServicesUnitResponse(
                            serviceUnit.getId(),
                            serviceUnit.getGranularityInMinutes(),
                            serviceUnit.getUser().getUsername());
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ServiceUnit> getServicesUnitByUsername(String username) {
        return serviceUnitRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public List<ServiceUnit> getAllServiceUnits() {
        return serviceUnitRepository.findAll();
    }

}
