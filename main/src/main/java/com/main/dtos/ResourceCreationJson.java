package com.main.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceCreationJson {
    private String unitService;
    private String typeName;
    private String name;
    private List<String> values; // Los valores de las características en orden
}