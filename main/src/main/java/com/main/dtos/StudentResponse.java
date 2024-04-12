package com.main.dtos;

import com.main.models.Student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponse {

    private Long id;
    private String documentType;
    private long document;
    private String email;
    private String firstName;
    private String middleName;
    private String firstLastName;
    private String middleLastName;
    private Long codeStudent;
    private String faculty;
    private String degreeProgram;

    public StudentResponse(Student student) {
        this.id = student.getId();
        this.documentType = student.getDocumentType().name();
        this.document = student.getDocument();
        this.email = student.getEmail();
        this.firstName = student.getFirstName();
        this.middleName = student.getMiddleName();
        this.firstLastName = student.getFirstLastName();
        this.middleLastName = student.getMiddleLastName();
        this.codeStudent = student.getCodeStudent();
        this.faculty = student.getFaculty();
        this.degreeProgram = student.getDegreeProgram();
    }
}
