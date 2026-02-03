
package com.ems.controller;

import com.ems.dto.LeaveRequestCreateDto;
import com.ems.dto.LeaveRequestResponseDto;
import com.ems.dto.LeaveStatusUpdateRequest;
import com.ems.entity.LeaveRequest;
import com.ems.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#dto.employeeId)")
    public ResponseEntity<LeaveRequestResponseDto> submitLeave(@Valid @RequestBody LeaveRequestCreateDto dto) {
        LeaveRequest saved = leaveService.submitLeaveRequest(dto);
        LeaveRequestResponseDto response = new LeaveRequestResponseDto(saved.getId(), saved.getEmployee().getId(), saved.getStartDate(), saved.getEndDate(), saved.getStatus(), saved.getReason(), saved.getCreationTimestamp());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LeaveRequestResponseDto> updateStatus(@PathVariable Long id, @Valid @RequestBody LeaveStatusUpdateRequest request) {

        LeaveRequest updated = null;
        try {
            updated = leaveService.updateLeaveStatus(id, request.status());
        } catch (BadRequestException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(toDto(updated));
    }

    @GetMapping("/employee/{empId}")
    public ResponseEntity<List<LeaveRequestResponseDto>> getByEmployee(@PathVariable Long empId) {
        List<LeaveRequestResponseDto> response = leaveService.getLeavesByEmployee(empId).stream().map(this::toDto).toList();
        return ResponseEntity.ok(response);
    }

    private LeaveRequestResponseDto toDto(LeaveRequest lr) {
        return new LeaveRequestResponseDto(
                lr.getId(),
                lr.getEmployee().getId(),
                lr.getStartDate(),
                lr.getEndDate(),
                lr.getStatus(),
                lr.getReason(),
                lr.getCreationTimestamp()
        );
    }
}
