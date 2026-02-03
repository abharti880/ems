package com.ems.controller;

import com.ems.dto.CreateDepartmentRequest;
import com.ems.dto.DepartmentSummary;
import com.ems.entity.Employee;
import com.ems.exception.ResourceNotFoundException;
import com.ems.service.DepartmentService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DepartmentController.class)
@AutoConfigureMockMvc
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartmentService departmentService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser(roles = "ADMIN")
    void listAll_success_admin() throws Exception {
        when(departmentService.getAllDepartmentSummaries())
                .thenReturn(List.of(new DepartmentSummary(1L, "IT")));

        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "HR")
    void listAll_success_hr() throws Exception {
        when(departmentService.getAllDepartmentSummaries())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk());
    }

    @Test
    void listAll_unauthorized() throws Exception {
        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void createDepartment_success() throws Exception {
        CreateDepartmentRequest request = new CreateDepartmentRequest();
        request.setName("IT");
        request.setLocation("Bangalore");

        when(departmentService.createDepartment(any()))
                .thenReturn(new DepartmentSummary(1L, "IT"));

        mockMvc.perform(post("/api/departments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDepartment_validationFailure() throws Exception {
        CreateDepartmentRequest request = new CreateDepartmentRequest();

        mockMvc.perform(post("/api/departments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void getDepartmentEmployees_success() throws Exception {
        Page<Employee> page = new PageImpl<>(List.of(new Employee()));

        when(departmentService.getDepartmentEmployees(eq(1L), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/departments/1/employees"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDepartmentEmployees_notFound() throws Exception {
        when(departmentService.getDepartmentEmployees(eq(99L), any()))
                .thenThrow(new ResourceNotFoundException("Department not found"));

        mockMvc.perform(get("/api/departments/99/employees"))
                .andExpect(status().isNotFound());
    }
}

