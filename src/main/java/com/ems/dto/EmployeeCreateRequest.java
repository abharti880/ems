package com.ems.dto;

import com.ems.Enums.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
