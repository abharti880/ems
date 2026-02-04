package com.ems.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmployeeAdminResponse extends EmployeeResponse {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
