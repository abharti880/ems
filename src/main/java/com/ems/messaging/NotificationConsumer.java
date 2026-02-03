package com.ems.messaging;

import com.ems.dto.NotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationConsumer {

    @RabbitListener(queues = "${ems.queue.employee}")
    public void consumeEmployeeNotification(NotificationDTO notification) {
        log.info("RABBITMQ: New Employee Event - ID: {}", notification.getRecipientId());
        simulateEmailSending(notification);
    }

    @RabbitListener(queues = "${ems.queue.leave}")
    public void consumeLeaveNotification(NotificationDTO notification) {
        log.info("RABBITMQ: Leave Status Update Event - ID: {}", notification.getRecipientId());
        simulateEmailSending(notification);
    }

    private void simulateEmailSending(NotificationDTO notification) {
        var template = """
            
            --------------------------------------------------
            SENDING EMAIL via SMTP Simulation
            To: %s <%s>
            Subject: EMS Notification [%s]
            Content: %s
            --------------------------------------------------
            """.formatted(
                notification.getRecipientName(),
                notification.getRecipientEmail(),
                notification.getType(),
                notification.getContent()
        );
        log.info(template);
    }
}