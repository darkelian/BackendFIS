package com.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.models.ServiceUnit;

public interface ServiceUnityRepository extends JpaRepository<ServiceUnit, Long> {

}
