package com.ems.controller;

import com.ems.dto.*;
import com.ems.entity.Employee;
import com.ems.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<EmployeeAdminResponse> getAllEmployees(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "fullName") String sortBy, @RequestParam(defaultValue = "asc") String direction, @RequestParam(required = false) Long departmentId) {
        return employeeService.getAllEmployees(page, size, sortBy, direction, departmentId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeAdminResponse getEmployeeById(@PathVariable Long id) {
        log.info("Fetching employee by id={}", id);
        return employeeService.getEmployeeById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeAdminResponse> createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        EmployeeAdminResponse response = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeAdminResponse> updateEmployee(@PathVariable Long id, @RequestBody EmployeeUpdateRequest request) {
        EmployeeAdminResponse response = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        log.info("Deleting employee id={}", id);
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name")
    public ResponseEntity<EmployeeSelfResponse> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        log.info("Fetching self profile email={}", email);

        EmployeeSelfResponse response = employeeService.getEmployeeProfile(email);
        return ResponseEntity.ok(response);
    }
}
