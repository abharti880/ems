package com.ems.controller;

import com.ems.dto.LeaveRequestCreateDto;
import com.ems.dto.LeaveRequestResponseDto;
import com.ems.dto.LeaveStatusUpdateRequest;
import com.ems.entity.Employee;
import com.ems.entity.LeaveRequest;
import com.ems.service.LeaveService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeaveController.class)
@AutoConfigureMockMvc
class LeaveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeaveService leaveService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitLeave_success_admin() throws Exception {

        LeaveRequestCreateDto dto = new LeaveRequestCreateDto(
                1L,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                "Vacation"
        );

        Employee emp = new Employee();
        emp.setId(1L);

        LeaveRequest saved = LeaveRequest.builder()
                .id(10L)
                .employee(emp)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .reason(dto.getReason())
                .status(LeaveRequest.LeaveStatus.PENDING)
                .creationTimestamp(LocalDateTime.now())
                .build();

        when(leaveService.submitLeaveRequest(any())).thenReturn(saved);

        mockMvc.perform(post("/api/leaves")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitLeave_validationFailure() throws Exception {

        LeaveRequestCreateDto dto = new LeaveRequestCreateDto(
                null, null, null, null
        );

        mockMvc.perform(post("/api/leaves")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateLeaveStatus_success() throws Exception {

        Employee emp = new Employee();
        emp.setId(1L);

        LeaveRequest updated = LeaveRequest.builder()
                .id(5L)
                .employee(emp)
                .status(LeaveRequest.LeaveStatus.APPROVED)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .creationTimestamp(LocalDateTime.now())
                .build();

        LeaveStatusUpdateRequest request =
                new LeaveStatusUpdateRequest("APPROVED");

        when(leaveService.updateLeaveStatus(eq(5L), eq("APPROVED")))
                .thenReturn(updated);

        mockMvc.perform(put("/api/leaves/5/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getLeavesByEmployee_success() throws Exception {

        Employee emp = new Employee();
        emp.setId(1L);

        LeaveRequest lr = LeaveRequest.builder()
                .id(1L)
                .employee(emp)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .status(LeaveRequest.LeaveStatus.PENDING)
                .creationTimestamp(LocalDateTime.now())
                .build();

        when(leaveService.getLeavesByEmployee(1L))
                .thenReturn(List.of(lr));

        mockMvc.perform(get("/api/leaves/employee/1"))
                .andExpect(status().isOk());
    }
}

