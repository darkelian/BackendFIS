package com.main.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class StudentRequest {
    @NotBlank(message = "El tipo de documento no puede estar vacío.")
    private String documentType;

    @NotNull(message = "El documento no puede ser nulo.")
    @Min(value = 0, message = "Documento no valido")
    private long document;

    @NotBlank(message = "El correo electrónico no puede estar vacío.")
    @Email(message = "Formato de correo electrónico inválido.")
    private String email;

    @NotBlank(message = "El primer nombre no puede estar vacío.")
    private String firstName;

    private String middleName; // Opcional, no necesita validación

    @NotBlank(message = "El primer apellido no puede estar vacío.")
    private String firstLastName;

    private String middleLastName; // Opcional, no necesita validación

    @NotBlank(message = "La contraseña no puede estar vacía.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    private String password;

    @Min(value = 10000000000L, message = "El código del estudiante debe tener 11 dígitos")
    @Max(value = 99999999999L, message = "El código del estudiante debe tener 11 dígitos")
    private Long codeStudent;

    @NotBlank(message = "El campo facultad no puede estar vacio")
    private String faculty;

    @NotBlank(message = "El campo Carrera no puede estar vacio")
    private String degreeProgram;
}
