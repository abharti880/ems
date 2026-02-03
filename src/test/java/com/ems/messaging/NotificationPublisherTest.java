package com.ems.messaging;

import com.ems.config.RabbitConfig;
import com.ems.dto.NotificationDTO;
import com.ems.messaging.NotificationType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private NotificationPublisher notificationPublisher;

    @Test
    void sendNewEmployeeNotification_success() {

        NotificationDTO dto = NotificationDTO.builder()
                .recipientId(1L)
                .recipientName("Test User")
                .recipientEmail("test@test.com")
                .type(NotificationType.NEW_EMPLOYEE)
                .content("Welcome to EMS")
                .build();

        assertDoesNotThrow(() ->
                notificationPublisher.sendNewEmployeeNotification(dto)
        );

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitConfig.EMPLOYEE_EXCHANGE),
                eq("employee.created"),
                eq(dto)
        );
    }

    @Test
    void sendNewEmployeeNotification_failureHandledGracefully() {

        NotificationDTO dto = NotificationDTO.builder()
                .recipientId(1L)
                .type(NotificationType.NEW_EMPLOYEE)
                .build();

        doThrow(new RuntimeException("RabbitMQ down"))
                .when(rabbitTemplate)
                .convertAndSend(
                        eq(RabbitConfig.EMPLOYEE_EXCHANGE),
                        eq("employee.created"),
                        eq(dto)
                );

        assertDoesNotThrow(() ->
                notificationPublisher.sendNewEmployeeNotification(dto)
        );
    }

    @Test
    void sendLeaveStatusNotification_success() {

        NotificationDTO dto = NotificationDTO.builder()
                .recipientId(2L)
                .recipientName("Test User")
                .recipientEmail("test@test.com")
                .type(NotificationType.LEAVE_STATUS_UPDATE)
                .content("Leave approved")
                .build();

        assertDoesNotThrow(() ->
                notificationPublisher.sendLeaveStatusNotification(dto)
        );

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitConfig.LEAVE_EXCHANGE),
                eq("leave.updated"),
                eq(dto)
        );
    }

    @Test
    void sendLeaveStatusNotification_failureHandledGracefully() {

        NotificationDTO dto = NotificationDTO.builder()
                .recipientId(2L)
                .type(NotificationType.LEAVE_STATUS_UPDATE)
                .build();

        doThrow(new RuntimeException("RabbitMQ down"))
                .when(rabbitTemplate)
                .convertAndSend(
                        eq(RabbitConfig.LEAVE_EXCHANGE),
                        eq("leave.updated"),
                        eq(dto)
                );

        assertDoesNotThrow(() ->
                notificationPublisher.sendLeaveStatusNotification(dto)
        );
    }
}
