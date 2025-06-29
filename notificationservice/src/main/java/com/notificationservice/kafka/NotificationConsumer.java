package com.notificationservice.kafka;

import com.notificationservice.dto.kafka.NotificationRequestDto;
import com.notificationservice.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
        topics = "${spring.kafka.topic.notifications}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeNotifications(NotificationRequestDto notification, Acknowledgment acknowledgment) {
        try {
            log.info("Message received: {}", notification);
            notificationService.processNotification(notification);
            log.info("Message sent: {}", notification);
            acknowledgment.acknowledge();
            log.info("acknowledgment sent: {}", notification);
        } catch (Exception e) {
            log.error("Error processing notification: {}", notification, e);
        }
    }
}
