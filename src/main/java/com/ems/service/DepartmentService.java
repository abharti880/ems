
package com.ems.service;

import com.ems.dto.CreateDepartmentRequest;
import com.ems.dto.DepartmentSummary;
import com.ems.entity.Department;
import com.ems.entity.Employee;
import com.ems.exception.ResourceNotFoundException;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public List<DepartmentSummary> getAllDepartmentSummaries() {
        return departmentRepository.findAllSummaries();
    }

    @Transactional
    public DepartmentSummary createDepartment(CreateDepartmentRequest request) {

        if (departmentRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Department already exists: " + request.getName());
        }

        Department department = Department.builder()
                .name(request.getName())
                .location(request.getLocation())
                .build();

        Department saved = departmentRepository.save(department);

        return new DepartmentSummary(saved.getId(), saved.getName());
    }

    public Page<Employee> getDepartmentEmployees(Long deptId, Pageable pageable) {

        Page<Employee> employees =
                employeeRepository.findByDepartmentId(deptId, pageable);

        if (employees.isEmpty() && !departmentRepository.existsById(deptId)) {
            throw new ResourceNotFoundException("Department not found with id: " + deptId);
        }

        return employees;
    }
}

