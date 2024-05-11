package com.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.models.Resource;

public interface ResourceRepository extends JpaRepository<Resource, Long>{
    
}
