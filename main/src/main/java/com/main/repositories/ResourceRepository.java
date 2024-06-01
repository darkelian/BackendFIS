package com.main.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.models.Resource;
import com.main.models.ResourceStatus;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    // Método para buscar recursos por su estado
    List<Resource> findByStatus(ResourceStatus status);

    Optional<Resource> findByIdAndStatus(Long id, String status);
}
