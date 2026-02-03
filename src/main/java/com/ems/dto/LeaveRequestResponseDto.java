package com.ems.dto;

import com.ems.entity.LeaveRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LeaveRequestResponseDto {

    private Long id;
    private Long employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveRequest.LeaveStatus status;
    private String reason;
    private LocalDateTime createdAt;
}

