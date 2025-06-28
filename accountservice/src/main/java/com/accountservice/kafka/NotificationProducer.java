package com.accountservice.kafka;

import com.accountservice.dto.kafka.NotificationRequestDto;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProducer {

    @Value("${spring.kafka.topic.notifications}")
    String notificationsTopic;

    private final KafkaTemplate<String, NotificationRequestDto> kafkaTemplate;
    private final MeterRegistry meterRegistry;

    public void sendNotifications(NotificationRequestDto notification, String login) {
        try {
            kafkaTemplate.send(notificationsTopic, notification);
            log.info("Notification sent to topic {}", notificationsTopic);
        } catch (Exception e) {
            meterRegistry.counter("notification_failed","login", login).increment();
            log.error("Error sending notification", e);
        }
    }
}