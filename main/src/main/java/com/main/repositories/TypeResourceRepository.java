package com.main.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.main.models.TypeResource;

public interface TypeResourceRepository extends JpaRepository<TypeResource, Long> {
    // Obtener los tipos de recursos de una unidad de servicio
    List<TypeResource> findByServiceUnitId(Long serviceUnitId);

    // Buscar un tipo de recurso por nombre
    @Query("SELECT tr FROM TypeResource tr WHERE LOWER(tr.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<TypeResource> findByName(@Param("name") String name);

    // Buscar un tipo de recurso por nombre y unidad de servicio
    Optional<TypeResource> findByNameAndServiceUnitId(String name, Long serviceUnitId);
}
