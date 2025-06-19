package com.cashservice.kafka;

import com.cashservice.dto.kafka.NotificationRequestDto;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
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

        notificationProducer.sendNotification(notification);

        verify(kafkaTemplate).send("notifications", notification);
    }

    @Test
    void sendNotification_WithMockProducer_ShouldProduceMessage() {

        NotificationRequestDto notification = new NotificationRequestDto("test@example.com", "message");

        NotificationProducer producerWithMock = new NotificationProducer(spy(new KafkaTemplate<>(() -> mockProducer)));
        producerWithMock.notificationsTopic = "notifications";

        producerWithMock.sendNotification(notification);

        List<ProducerRecord<String, NotificationRequestDto>> records = mockProducer.history();
        assertEquals(1, records.size());
        assertEquals("notifications", records.getFirst().topic());
        assertEquals(notification.getEmail(), records.getFirst().value().getEmail());
        assertEquals(notification.getMessage(), records.getFirst().value().getMessage());
    }
}
