package com.ems.service;

import com.ems.dto.CreateDepartmentRequest;
import com.ems.dto.DepartmentSummary;
import com.ems.entity.Department;
import com.ems.entity.Employee;
import com.ems.exception.ResourceNotFoundException;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.EmployeeRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @InjectMocks
    private DepartmentService departmentService;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeRepository employeeRepository;


    @Test
    void getAllDepartmentSummaries_success() {
        when(departmentRepository.findAllSummaries())
                .thenReturn(List.of(new DepartmentSummary(1L, "IT")));

        List<DepartmentSummary> result =
                departmentService.getAllDepartmentSummaries();

        assertEquals(1, result.size());
    }

    @Test
    void createDepartment_success() {
        CreateDepartmentRequest request = new CreateDepartmentRequest();
        request.setName("IT");
        request.setLocation("Bangalore");

        Department saved = Department.builder()
                .id(1L)
                .name("IT")
                .location("Bangalore")
                .build();

        when(departmentRepository.existsByNameIgnoreCase("IT"))
                .thenReturn(false);
        when(departmentRepository.save(any(Department.class)))
                .thenReturn(saved);

        DepartmentSummary summary =
                departmentService.createDepartment(request);

        assertEquals("IT", summary.getName());
    }

    @Test
    void createDepartment_alreadyExists() {
        CreateDepartmentRequest request = new CreateDepartmentRequest();
        request.setName("IT");

        when(departmentRepository.existsByNameIgnoreCase("IT"))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> departmentService.createDepartment(request));
    }


    @Test
    void getDepartmentEmployees_success() {
        Page<Employee> page = new PageImpl<>(List.of(new Employee()));

        when(employeeRepository.findByDepartmentId(eq(1L), any(PageRequest.class)))
                .thenReturn(page);

        Page<Employee> result =
                departmentService.getDepartmentEmployees(1L, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getDepartmentEmployees_departmentNotFound() {
        when(employeeRepository.findByDepartmentId(eq(99L), any(PageRequest.class)))
                .thenReturn(Page.empty());
        when(departmentRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> departmentService.getDepartmentEmployees(99L, PageRequest.of(0, 10)));
    }
}
