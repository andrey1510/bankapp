package com.cashservice.kafka;

import com.cashservice.dto.kafka.NotificationRequestDto;
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

    public void sendNotification(NotificationRequestDto notification) {
        try {
            kafkaTemplate.send(notificationsTopic, notification);
        } catch (Exception e) {
            log.error("Error sending notification", e);
        }
    }
}
