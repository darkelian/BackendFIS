package com.main.dtos;

import com.main.models.Employee;
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
    private ServicesUnitResponse serviceUnit;

    public EmployeeResponse(Employee employee) {
        this.id = employee.getId();
        this.documentType = employee.getDocumentType().name();
        this.document = employee.getDocument();
        this.email = employee.getEmail();
        this.firstName = employee.getFirstName();
        this.middleName = employee.getMiddleName();
        this.firstLastName = employee.getFirstLastName();
        this.middleLastName = employee.getMiddleLastName();
        if (employee.getServiceUnit() != null) {
            this.serviceUnit = new ServicesUnitResponse(employee.getServiceUnit().getId(),
                    employee.getServiceUnit().getGranularityInMinutes(), employee.getServiceUnit().getName());
        }
    }
}
