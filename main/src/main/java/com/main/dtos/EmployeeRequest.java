package com.main.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequest {
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

    @NotNull(message = "La unidad de servicio no puede estar vacía.")
    private Long serviceUnitId;
}
