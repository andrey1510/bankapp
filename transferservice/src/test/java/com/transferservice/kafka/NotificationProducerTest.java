package com.transferservice.kafka;

import com.transferservice.dto.kafka.NotificationRequestDto;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationProducerTest {

    @Mock
    private KafkaTemplate<String, NotificationRequestDto> kafkaTemplate;

    @InjectMocks
    private NotificationProducer notificationProducer;

    private MockProducer<String, NotificationRequestDto> mockProducer;

    @BeforeEach
    void setUp() {
        mockProducer = new MockProducer<>(
            true,
            new StringSerializer(),
            new org.springframework.kafka.support.serializer.JsonSerializer<>()
        );
    }

    @Test
    void sendNotification_ShouldSendMessageToKafkaSuccessfully() {

        NotificationRequestDto notification = new NotificationRequestDto("test@example.com", "message");
        ReflectionTestUtils.setField(notificationProducer, "notificationsTopic", "notifications");

        notificationProducer.sendNotification(notification, "login");

        verify(kafkaTemplate).send("notifications", notification);
    }

}
