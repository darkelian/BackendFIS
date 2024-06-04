package com.main.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MostReservedRequestDTO {
    
    private String resourceName;
    
    @NotBlank(message = "La fecha es obligatoria")
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "La fecha debe estar en el formato dd/MM/yyyy")
    private String startDate;

    @NotBlank(message = "La fecha es obligatoria")
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "La fecha debe estar en el formato dd/MM/yyyy")
    private String endDate;
}
