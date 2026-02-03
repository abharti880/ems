package com.ems.messaging;

import com.ems.dto.NotificationDTO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class NotificationConsumerTest {

    private final NotificationConsumer notificationConsumer =
            new NotificationConsumer();

    @Test
    void consumeEmployeeNotification_success() {

        NotificationDTO dto = NotificationDTO.builder()
                .recipientId(1L)
                .recipientName("Test User")
                .recipientEmail("test@test.com")
                .type(NotificationType.NEW_EMPLOYEE)
                .content("Welcome to EMS")
                .build();

        assertDoesNotThrow(() ->
                notificationConsumer.consumeEmployeeNotification(dto)
        );
    }

    @Test
    void consumeLeaveNotification_success() {

        NotificationDTO dto = NotificationDTO.builder()
                .recipientId(2L)
                .recipientName("Test User")
                .recipientEmail("test@test.com")
                .type(NotificationType.LEAVE_STATUS_UPDATE)
                .content("Your leave has been approved")
                .build();

        assertDoesNotThrow(() ->
                notificationConsumer.consumeLeaveNotification(dto)
        );
    }
}
