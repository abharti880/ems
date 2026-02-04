package com.ems.dto;

import com.ems.Enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    private Long id;
    private String fullName;
    private String email;
    private DepartmentSummary department;
    private BigDecimal salary;
    private LocalDate joiningDate;
    private Role role;
}
