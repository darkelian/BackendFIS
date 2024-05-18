package com.main.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeatureDTO {
    private Long id;
    private String value;
    private String name;
    private String type;
}
