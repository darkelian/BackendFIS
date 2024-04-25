package com.main.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.main.models.ServiceUnit;
@Repository
public interface ServiceUnityRepository extends JpaRepository<ServiceUnit, Long> {

    @Query("SELECT su FROM ServiceUnit su WHERE su.user.username = :username")
    Optional<ServiceUnit> findByUsername(@Param("username") String username);
}
