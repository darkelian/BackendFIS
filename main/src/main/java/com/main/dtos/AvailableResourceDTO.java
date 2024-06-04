package com.main.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableResourceDTO {
    private Long id;
    private String name;
    private String typeName;
    private String serviceUnitName;
    private String status;
    private int availableQuantity;
}