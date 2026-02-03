
package com.ems.controller;

import com.ems.dto.CreateDepartmentRequest;
import com.ems.dto.DepartmentSummary;
import com.ems.entity.Department;
import com.ems.entity.Employee;
import com.ems.service.DepartmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<List<DepartmentSummary>> listAll() {
        return ResponseEntity.ok(departmentService.getAllDepartmentSummaries());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<DepartmentSummary> create(
            @Valid @RequestBody CreateDepartmentRequest request) {

        DepartmentSummary created = departmentService.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/{id}/employees")
    public ResponseEntity<Page<Employee>> getEmployees(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {

        return ResponseEntity.ok(
                departmentService.getDepartmentEmployees(id, PageRequest.of(page, size))
        );
    }
}

