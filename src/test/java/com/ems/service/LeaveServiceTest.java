package com.ems.service;

import com.ems.dto.LeaveRequestCreateDto;
import com.ems.entity.Employee;
import com.ems.entity.LeaveRequest;
import com.ems.messaging.NotificationPublisher;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.LeaveRequestRepository;

import jakarta.persistence.EntityNotFoundException;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

    @InjectMocks
    private LeaveService leaveService;

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private NotificationPublisher notificationPublisher;

    @Test
    void submitLeave_success() {

        LeaveRequestCreateDto dto = new LeaveRequestCreateDto(
                1L,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                "Vacation"
        );

        Employee emp = new Employee();
        emp.setId(1L);

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(emp));
        when(leaveRequestRepository.findByEmployeeId(1L))
                .thenReturn(List.of());
        when(leaveRequestRepository.save(any(LeaveRequest.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        LeaveRequest saved =
                leaveService.submitLeaveRequest(dto);

        assertNotNull(saved);
        assertEquals(LeaveRequest.LeaveStatus.PENDING, saved.getStatus());
    }

    @Test
    void submitLeave_employeeNotFound() {

        LeaveRequestCreateDto dto = new LeaveRequestCreateDto(
                99L,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                "Vacation"
        );

        when(employeeRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> leaveService.submitLeaveRequest(dto));
    }

    @Test
    void submitLeave_overlappingDates() {

        LeaveRequestCreateDto dto = new LeaveRequestCreateDto(
                1L,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                "Vacation"
        );

        Employee emp = new Employee();
        emp.setId(1L);

        LeaveRequest existing = LeaveRequest.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(3))
                .status(LeaveRequest.LeaveStatus.APPROVED)
                .build();

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(emp));
        when(leaveRequestRepository.findByEmployeeId(1L))
                .thenReturn(List.of(existing));

        assertThrows(IllegalArgumentException.class,
                () -> leaveService.submitLeaveRequest(dto));
    }


    @Test
    void updateLeaveStatus_success() throws Exception {

        Employee emp = new Employee();
        emp.setFullName("Test");
        emp.setEmail("test@test.com");

        LeaveRequest leave = LeaveRequest.builder()
                .id(1L)
                .employee(emp)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .status(LeaveRequest.LeaveStatus.PENDING)
                .build();

        when(leaveRequestRepository.findById(1L))
                .thenReturn(Optional.of(leave));

        LeaveRequest updated =
                leaveService.updateLeaveStatus(1L, "APPROVED");

        assertEquals(LeaveRequest.LeaveStatus.APPROVED, updated.getStatus());
        verify(notificationPublisher).sendNewEmployeeNotification(any());
    }

    @Test
    void updateLeaveStatus_invalidStatus() {

        assertThrows(BadRequestException.class,
                () -> leaveService.updateLeaveStatus(1L, "INVALID"));
    }


    @Test
    void getLeavesByEmployee_success() {

        when(leaveRequestRepository.findByEmployeeId(1L))
                .thenReturn(List.of(new LeaveRequest()));

        List<LeaveRequest> result =
                leaveService.getLeavesByEmployee(1L);

        assertEquals(1, result.size());
    }
}
