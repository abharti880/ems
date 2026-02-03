package com.ems.dto;

import com.ems.Enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class EmployeeResponse {
    private Long id;
    private String fullName;
    private String email;
    private DepartmentSummary department;
    private BigDecimal salary;
    private LocalDate joiningDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Role role;
}

