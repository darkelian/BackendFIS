package com.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.models.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
