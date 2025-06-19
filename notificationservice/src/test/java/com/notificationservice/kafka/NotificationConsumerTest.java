package com.notificationservice.kafka;

import com.notificationservice.dto.kafka.NotificationRequestDto;
import com.notificationservice.services.NotificationService;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private NotificationConsumer notificationConsumer;

    private MockConsumer<String, NotificationRequestDto> mockConsumer;

    @BeforeEach
    void setUp() {
        mockConsumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
    }

    @Test
    void consumeNotifications_ShouldProcessNotificationSuccessfully() {

        NotificationRequestDto request = new NotificationRequestDto("test@example.com", "Test message");

        notificationConsumer.consumeNotifications(request, acknowledgment);

        verify(notificationService).processNotification(request);
        verify(acknowledgment).acknowledge();
        verifyNoMoreInteractions(notificationService, acknowledgment);
    }

    @Test
    void consumeNotifications_ShouldHandleExceptionGracefully() {

        NotificationRequestDto request = new NotificationRequestDto("test@example.com", "Test message");

        doThrow(new RuntimeException("Test exception")).when(notificationService).processNotification(request);

        notificationConsumer.consumeNotifications(request, acknowledgment);

        verify(notificationService).processNotification(request);
        verify(acknowledgment, never()).acknowledge();
    }

}
