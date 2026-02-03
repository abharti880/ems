
package com.ems.messaging;

import com.ems.config.RabbitConfig;
import com.ems.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void sendNewEmployeeNotification(NotificationDTO dto) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EMPLOYEE_EXCHANGE,
                    "employee.created",
                    dto
            );
            log.info("Published employee notification: {}", dto);
        } catch (Exception e) {
            log.error("Failed to publish employee notification", e);
        }
    }

    public void sendLeaveStatusNotification(NotificationDTO dto) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.LEAVE_EXCHANGE,
                    "leave.updated",
                    dto
            );
            log.info("Published leave notification: {}", dto);
        } catch (Exception e) {
            log.error("Failed to publish leave notification", e);
        }
    }
}
