package com.main.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.main.models.ServiceUnit;

@Repository
public interface ServiceUnitRepository extends JpaRepository<ServiceUnit, Long> {

    // Busca una unidad de servicio asociada a un usuario específico basado en el
    // nombre de usuario
    @Query("SELECT su FROM ServiceUnit su JOIN su.user u WHERE u.username = :username")
    Optional<ServiceUnit> findByUsername(@Param("username") String username);

    // Busca una unidad de servicio asociada a un empleado específico basado en el
    // nombre de usuario del empleado.
    @Query("SELECT su FROM ServiceUnit su JOIN su.employees e JOIN e.user u WHERE u.username = :username")
    Optional<ServiceUnit> findByEmployeeUsername(@Param("username") String username);

}
