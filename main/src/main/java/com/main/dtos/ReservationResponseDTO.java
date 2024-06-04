package com.main.dtos;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponseDTO {
    private Long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private LocalDate reservationDate;
    private int quantity;
    private Long resourceId;
    private Long studentId;
    private Long employeeId;
    private String resourceName;
    private String studentName;
    private String employeeName;
}
