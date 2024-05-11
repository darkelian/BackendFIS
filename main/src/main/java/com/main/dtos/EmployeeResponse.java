package com.main.dtos;

import com.main.models.Employee;
import com.main.models.ServiceUnit;

import lombok.Data;

@Data
public class EmployeeResponse {
    private Long id;
    private String documentType;
    private long document;
    private String email;
    private String firstName;
    private String middleName;
    private String firstLastName;
    private String middleLastName;
    private ServiceUnit serviceUnit;

    public EmployeeResponse(Employee employee) {
        this.id = employee.getId();
        this.documentType = employee.getDocumentType().name();
        this.document = employee.getDocument();
        this.email = employee.getEmail();
        this.firstName = employee.getFirstName();
        this.middleName = employee.getMiddleName();
        this.firstLastName = employee.getFirstLastName();
        this.middleLastName = employee.getMiddleLastName();
    }
}
