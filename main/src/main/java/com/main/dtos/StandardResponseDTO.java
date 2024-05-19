package com.main.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardResponseDTO {
    private boolean success;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message = null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data = null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> errors = null;
    private int count;

    public StandardResponseDTO fullSuccess(Object data) {
        this.success = true;
        this.data = data;
        // Comprobar si data es una instancia de List
        if (data instanceof List) {
            // Establecer count como el tamaño de la lista
            this.count = ((List<?>) data).size();
        } else {
            // Si no es una lista, establecer count a 1
            this.count = 1;
        }
        return this;
    }

    public StandardResponseDTO failSuccess(String message) {
        this.success = false;
        this.message = message;
        this.count = 1;
        return this;
    }
}
