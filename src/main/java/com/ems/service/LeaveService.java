package com.ems.service;

import com.ems.dto.LeaveRequestCreateDto;
import com.ems.dto.NotificationDTO;
import com.ems.entity.Employee;
import com.ems.entity.LeaveRequest;
import com.ems.exception.ResourceNotFoundException;
import com.ems.messaging.NotificationPublisher;
import com.ems.messaging.NotificationType;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.LeaveRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final NotificationPublisher notificationPublisher;
    private final EmployeeRepository employeeRepository;

    /* ============================
       SUBMIT LEAVE
       ============================ */

    @Transactional
    public LeaveRequest submitLeaveRequest(LeaveRequestCreateDto dto) {
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        validateLeaveDates(dto.getStartDate(), dto.getEndDate(), employee.getId());

        LeaveRequest request = LeaveRequest.builder()
                .employee(employee)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .reason(dto.getReason())
                .status(LeaveRequest.LeaveStatus.PENDING)
                .build();

        return leaveRequestRepository.save(request);
    }

    /* ============================
       UPDATE LEAVE STATUS
       ============================ */

    @Transactional
    public LeaveRequest updateLeaveStatus(Long id, String statusValue) throws BadRequestException {

        LeaveRequest.LeaveStatus status;
        try {
            status = LeaveRequest.LeaveStatus.valueOf(statusValue.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid leave status: " + statusValue);
        }

        LeaveRequest leave = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leave request not found"));

        leave.setStatus(status);
        notificationPublisher.sendNewEmployeeNotification(
                NotificationDTO.builder()
                        .type(NotificationType.LEAVE_STATUS_UPDATE)
                        .recipientId(leave.getId())
                        .recipientName(leave.getEmployee().getFullName())
                        .recipientEmail(leave.getEmployee().getEmail())
                        .content(buildLeaveStatusContent(leave))
                        .build()
        );
        return leave;
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> getLeavesByEmployee(Long empId) {

        return leaveRequestRepository.findByEmployeeId(empId);
    }

    private void validateLeaveDates(LocalDate start, LocalDate end, Long employeeId) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and End dates are mandatory");
        }

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (start.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot apply leave in the past");
        }

        var existingLeaves = leaveRequestRepository
                .findByEmployeeId(employeeId);

        boolean overlap = existingLeaves.stream()
                .filter(l -> l.getStatus() != LeaveRequest.LeaveStatus.REJECTED)
                .anyMatch(l ->
                        !start.isAfter(l.getEndDate())
                                && !end.isBefore(l.getStartDate())
                );

        if (overlap) {
            throw new IllegalArgumentException(
                    "Leave request overlaps with existing request");
        }
    }

    private String buildLeaveStatusContent(LeaveRequest leave) {
        return String.format(
                "Your leave request (ID: %d) for %s to %s has been %s.",
                leave.getId(),
                leave.getStartDate(),
                leave.getEndDate(),
                leave.getStatus().name()
        );
    }
}
