package com.main.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.main.models.Feature;

public interface FeatureRepository extends JpaRepository<Feature, Long> {

    // Busca todas las características asociadas a un tipo de recurso específico
    // basado en el nombre del tipo de recurso.
    @Query("SELECT f FROM Feature f WHERE f.typeResource.name = :typeName")
    List<Feature> findByTypeName(@Param("typeName") String typeName);

    // Buscar todas las características asociadas a un tipo de recurso específico por id
    @Query("SELECT f FROM Feature f LEFT JOIN FETCH f.resourceFeatures rf WHERE f.typeResource.id = :typeId")
    List<Feature> findByTypeResourceIdWithResourceFeatures(@Param("typeId") Long typeId);
}
