package com.ems.controller;

import com.ems.Enums.Role;
import com.ems.controller.EmployeeController;
import com.ems.dto.EmployeeCreateRequest;
import com.ems.dto.EmployeeResponse;
import com.ems.dto.EmployeeUpdateRequest;
import com.ems.exception.ResourceNotFoundException;
import com.ems.service.EmployeeService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import org.springframework.http.MediaType;

import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllEmployees_success() throws Exception {
        Page<EmployeeResponse> page =
                new PageImpl<>(List.of(new EmployeeResponse()));

        when(employeeService.getAllEmployees(0, 10, "fullName", "asc", null))
                .thenReturn(page);

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllEmployees_nullDepartmentId() throws Exception {
        when(employeeService.getAllEmployees(anyInt(), anyInt(), anyString(), anyString(), isNull()))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/api/employees")
                        .param("departmentId", ""))
                .andExpect(status().isOk());
    }

    @Test
    void getAllEmployees_unauthorized() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void getEmployeeById_success() throws Exception {
        when(employeeService.getEmployeeById(1L))
                .thenReturn(new EmployeeResponse());

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEmployeeById_notFound() throws Exception {
        when(employeeService.getEmployeeById(99L))
                .thenThrow(new ResourceNotFoundException("Employee not found"));

        mockMvc.perform(get("/api/employees/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEmployeeById_invalidIdFormat() throws Exception {
        mockMvc.perform(get("/api/employees/abc"))
                .andExpect(status().isBadRequest());
    }



    @Test
    @WithMockUser(roles = "ADMIN")
    void createEmployee_success() throws Exception {

        EmployeeCreateRequest request = new EmployeeCreateRequest();
        request.setFullName("Test User");
        request.setEmail("test@test.com");
        request.setDepartmentId(1L);
        request.setJoiningDate(LocalDate.now());
        request.setPassword("StrongPassword@123");
        request.setRole(Role.ROLE_USER);
        request.setSalary(BigDecimal.valueOf(50000));

        EmployeeResponse response = new EmployeeResponse();
        response.setId(1L);
        response.setFullName("Test User");
        response.setEmail("test@test.com");

        when(employeeService.createEmployee(any())).thenReturn(response);

        mockMvc.perform(post("/api/employees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(employeeService).createEmployee(any());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void createEmployee_validationFailure() throws Exception {
        EmployeeCreateRequest request = new EmployeeCreateRequest();

        mockMvc.perform(post("/api/employees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateEmployee_success() throws Exception {
        when(employeeService.updateEmployee(eq(1L), any()))
                .thenReturn(new EmployeeResponse());

        mockMvc.perform(put("/api/employees/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new EmployeeUpdateRequest())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateEmployee_notFound() throws Exception {
        when(employeeService.updateEmployee(eq(1L), any()))
                .thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(put("/api/employees/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new EmployeeUpdateRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteEmployee_success() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employees/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteEmployee_notFound() throws Exception {
        doThrow(new ResourceNotFoundException("Not found"))
                .when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employees/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(username = "test@test.com", roles = "ADMIN")
    void getMyProfile_success() throws Exception {
        when(employeeService.getEmployeeProfile("test@test.com"))
                .thenReturn(new EmployeeResponse());

        mockMvc.perform(get("/api/employees/me"))
                .andExpect(status().isOk());
    }

    @Test
    void getMyProfile_unauthenticated() throws Exception {
        mockMvc.perform(get("/api/employees/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllEmployees_withAllParams() throws Exception {

        when(employeeService.getAllEmployees(1, 5, "email", "desc", 10L))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/api/employees")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sortBy", "email")
                        .param("direction", "desc")
                        .param("departmentId", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createEmployee_nullBody() throws Exception {

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteEmployee_unauthorized() throws Exception {
        mockMvc.perform(delete("/api/employees/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "USER")
    void getMyProfile_selfAccess_nonAdmin() throws Exception {

        when(employeeService.getEmployeeProfile("user@test.com"))
                .thenReturn(new EmployeeResponse());

        mockMvc.perform(get("/api/employees/me"))
                .andExpect(status().isOk());
    }


}

