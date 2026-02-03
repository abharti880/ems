package com.ems.bootstrap;

import com.ems.Enums.Role;
import com.ems.entity.Employee;
import com.ems.repository.EmployeeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createAdmin() {
        if (employeeRepository.findByEmail("admin@ems.com").isPresent()) {
            return;
        }

        Employee admin = Employee.builder()
                .fullName("System Admin")
                .email("admin@ems.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ROLE_ADMIN)
                .salary(BigDecimal.valueOf(0))
                .joiningDate(LocalDate.now())
                .build();

        employeeRepository.save(admin);
    }
}
