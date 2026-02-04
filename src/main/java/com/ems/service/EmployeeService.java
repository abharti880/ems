package com.ems.service;

import com.ems.Enums.Role;
import com.ems.dto.*;
import com.ems.entity.Department;
import com.ems.entity.Employee;
import com.ems.exception.ResourceNotFoundException;
import com.ems.mapper.EmployeeMapper;
import com.ems.messaging.NotificationPublisher;
import com.ems.messaging.NotificationType;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final NotificationPublisher notificationPublisher;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    @Transactional(readOnly = true)
    public Page<EmployeeAdminResponse> getAllEmployees(int page, int size, String sortBy, String direction,
                                                       Long departmentId) {
        log.info("Fetching employees page={}, size={}, sortBy={}, direction={}, deptId={}", page, size, sortBy,
                direction, departmentId);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<Employee> employeePage = departmentId != null
                ? employeeRepository.findByDepartmentId(departmentId, pageable)
                : employeeRepository.findAll(pageable);
        return employeePage.map(employeeMapper::toAdminResponse);
    }

    @Transactional
    public EmployeeSelfResponse getEmployeeProfile(String email) {
        log.info("Fetching employee by email={}", email);
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Employee not found with email={}", email);
                    return new ResourceNotFoundException("Employee not found");
                });
        return employeeMapper.toSelfResponse(employee);
    }

    @Transactional
    public EmployeeAdminResponse getEmployeeById(Long id) {
        log.info("Fetching employee by id={}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with id={}", id);
                    return new ResourceNotFoundException("Employee not found");
                });

        return employeeMapper.toAdminResponse(employee);
    }

    public EmployeeAdminResponse createEmployee(EmployeeCreateRequest request) {

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid department id"));

        Employee employee = employeeMapper.toEntity(request);
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        employee.setDepartment(department);

        Employee saved = employeeRepository.save(employee);

        notificationPublisher.sendNewEmployeeNotification(
                NotificationDTO.builder()
                        .type(NotificationType.NEW_EMPLOYEE)
                        .recipientId(saved.getId())
                        .recipientName(saved.getFullName())
                        .recipientEmail(saved.getEmail())
                        .content(buildNewEmployeeContent(employee))
                        .build());

        return employeeMapper.toAdminResponse(saved);
    }

    @Transactional
    public EmployeeAdminResponse updateEmployee(Long id, EmployeeUpdateRequest request) {

        log.info("Updating employee id={}", id);

        Employee existing = employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        employeeMapper.updateEntityFromRequest(request, existing);

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid department id"));
            existing.setDepartment(department);
        }
        Employee saved = employeeRepository.save(existing);
        return employeeMapper.toAdminResponse(saved);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (employee.getRole() == Role.ROLE_ADMIN) {
            throw new IllegalArgumentException("Admin users cannot be deleted");
        }
        employeeRepository.delete(employee);
    }

    private String buildNewEmployeeContent(Employee employee) {
        return String.format(
                "Welcome to EMS!%n%n" +
                        "Employee ID: %d%n" +
                        "Name: %s%n" +
                        "Email: %s%n%n" +
                        "Your employee profile has been successfully created in the system.%n" +
                        "You can now access EMS to manage your profile, leave requests, and notifications.%n%n" +
                        "Purpose: New employee onboarding notification.",
                employee.getId(),
                employee.getFullName(),
                employee.getEmail());
    }

}
