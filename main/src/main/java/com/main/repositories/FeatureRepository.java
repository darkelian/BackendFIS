package com.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.models.Feature;

public interface FeatureRepository extends JpaRepository<Feature, Long> {

}
