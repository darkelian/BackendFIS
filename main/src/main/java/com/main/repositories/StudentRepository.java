package com.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.main.models.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

}
