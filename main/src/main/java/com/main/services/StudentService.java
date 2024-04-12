package com.main.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.main.dtos.StudentResponse;
import com.main.repositories.StudentRepository;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Service
@AllArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(StudentResponse::new) // Convertir cada Student a StudentResponse
                .collect(Collectors.toList());
    }
}
