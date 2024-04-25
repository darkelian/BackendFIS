package com.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.main.models.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
