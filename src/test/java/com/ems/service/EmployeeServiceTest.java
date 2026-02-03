package com.ems.service;

import com.ems.Enums.Role;
import com.ems.dto.EmployeeCreateRequest;
import com.ems.dto.EmployeeResponse;
import com.ems.dto.EmployeeUpdateRequest;
import com.ems.dto.NotificationDTO;
import com.ems.entity.Department;
import com.ems.entity.Employee;
import com.ems.exception.ResourceNotFoundException;
import com.ems.messaging.NotificationPublisher;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private NotificationPublisher notificationPublisher;

    @Mock
    private PasswordEncoder passwordEncoder;


    @Test
    void getAllEmployees_withoutDepartment() {
        Employee employee = new Employee();
        Page<Employee> page = new PageImpl<>(List.of(employee));

        when(employeeRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<EmployeeResponse> result =
                employeeService.getAllEmployees(0, 10, "fullName", "asc", null);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllEmployees_withDepartment() {
        Employee employee = new Employee();
        Page<Employee> page = new PageImpl<>(List.of(employee));

        when(employeeRepository.findByDepartmentId(eq(1L), any(PageRequest.class)))
                .thenReturn(page);

        Page<EmployeeResponse> result =
                employeeService.getAllEmployees(0, 10, "fullName", "desc", 1L);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getEmployeeProfile_success() {
        Employee employee = new Employee();
        employee.setEmail("test@test.com");

        when(employeeRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(employee));

        EmployeeResponse response =
                employeeService.getEmployeeProfile("test@test.com");

        assertNotNull(response);
    }

    @Test
    void getEmployeeProfile_notFound() {
        when(employeeRepository.findByEmail("x@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> employeeService.getEmployeeProfile("x@test.com"));
    }

    @Test
    void getEmployeeById_success() {
        Employee employee = new Employee();
        employee.setId(1L);

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        EmployeeResponse response =
                employeeService.getEmployeeById(1L);

        assertNotNull(response);
    }

    @Test
    void getEmployeeById_notFound() {
        when(employeeRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> employeeService.getEmployeeById(99L));
    }

    @Test
    void createEmployee_success() {
        EmployeeCreateRequest request = new EmployeeCreateRequest();
        request.setFullName("Test User");
        request.setEmail("test@test.com");
        request.setPassword("password");
        request.setRole(Role.ROLE_USER);
        request.setSalary(BigDecimal.valueOf(50000));
        request.setJoiningDate(LocalDate.now());
        request.setDepartmentId(1L);

        Department department = new Department();
        department.setId(1L);

        Employee saved = new Employee();
        saved.setId(10L);
        saved.setFullName("Test User");
        saved.setEmail("test@test.com");

        when(employeeRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(employeeRepository.save(any(Employee.class))).thenReturn(saved);

        EmployeeResponse response = employeeService.createEmployee(request);

        assertNotNull(response);
        verify(notificationPublisher).sendNewEmployeeNotification(any(NotificationDTO.class));
    }

    @Test
    void createEmployee_emailAlreadyExists() {
        EmployeeCreateRequest request = new EmployeeCreateRequest();
        request.setEmail("test@test.com");

        when(employeeRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> employeeService.createEmployee(request));
    }

    @Test
    void createEmployee_invalidDepartment() {
        EmployeeCreateRequest request = new EmployeeCreateRequest();
        request.setEmail("test@test.com");
        request.setDepartmentId(99L);

        when(employeeRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> employeeService.createEmployee(request));
    }

    @Test
    void updateEmployee_success_partialUpdate() {
        Employee existing = new Employee();
        existing.setId(1L);

        Department department = new Department();
        department.setId(2L);

        EmployeeUpdateRequest request = new EmployeeUpdateRequest();
        request.setFullName("Updated Name");
        request.setDepartmentId(2L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(existing);

        EmployeeResponse response =
                employeeService.updateEmployee(1L, request);

        assertNotNull(response);
    }

    @Test
    void updateEmployee_notFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> employeeService.updateEmployee(1L, new EmployeeUpdateRequest()));
    }

    @Test
    void deleteEmployee_success() {
        Employee employee = new Employee();
        employee.setRole(Role.ROLE_USER);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertDoesNotThrow(() -> employeeService.deleteEmployee(1L));
        verify(employeeRepository).delete(employee);
    }

    @Test
    void deleteEmployee_adminNotAllowed() {
        Employee employee = new Employee();
        employee.setRole(Role.ROLE_ADMIN);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThrows(IllegalArgumentException.class,
                () -> employeeService.deleteEmployee(1L));
    }

    @Test
    void deleteEmployee_notFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> employeeService.deleteEmployee(99L));
    }
}
