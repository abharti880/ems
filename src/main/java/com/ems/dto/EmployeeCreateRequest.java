package com.ems.dto;

import com.ems.Enums.Role;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class EmployeeCreateRequest {

    @NotBlank
    private String fullName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotNull
    private Role role;

    @NotNull
    private Long departmentId;

    @NotNull
    @Positive
    private BigDecimal salary;

    @NotNull
    private LocalDate joiningDate;

}

