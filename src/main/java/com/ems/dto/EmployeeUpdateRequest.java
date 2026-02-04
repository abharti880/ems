package com.ems.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeUpdateRequest {

    private String fullName;

    @Positive
    private BigDecimal salary;

    private Long departmentId;

}
