package com.main.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Service
@AllArgsConstructor
public class ResourceServices {
    public String createTypeResource(String entity) {
        return "createTypeResource";
    }
}
