package com.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.main.models.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByCodeStudent(Long codeStudent);
}
