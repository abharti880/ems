package com.ems.dto;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EmployeeUpdateRequest {

    private String fullName;

    @Positive
    private BigDecimal salary;

    private Long departmentId;

}
