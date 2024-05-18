package com.main.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.models.TypeResource;

public interface TypeResourceRepository extends JpaRepository<TypeResource, Long> {
    List<TypeResource> findByServiceUnitId(Long serviceUnitId);
}
