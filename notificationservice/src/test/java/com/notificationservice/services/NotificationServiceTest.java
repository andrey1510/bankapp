package com.notificationservice.services;

import com.notificationservice.dto.NotificationRequestDto;
import com.notificationservice.entities.Notification;
import com.notificationservice.repositories.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private JavaMailSender mailSender;

    @Captor
    private ArgumentCaptor<Notification> notificationCaptor;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> mailMessageCaptor;

    private NotificationServiceImpl notificationService;

    private NotificationRequestDto validRequest;
    private Notification savedNotification;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl(notificationRepository, mailSender);
        validRequest = new NotificationRequestDto("test@example.com", "Test message");
        savedNotification = Notification.builder()
            .email(validRequest.email())
            .message(validRequest.message())
            .isSent(false)
            .build();
    }

    @Test
    void processNotification_ShouldSaveAndSendNotification() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        notificationService.processNotification(validRequest);

        verify(notificationRepository, times(2)).save(notificationCaptor.capture());
        verify(mailSender).send(mailMessageCaptor.capture());

        List<Notification> capturedNotifications = notificationCaptor.getAllValues();
        Notification firstSave = capturedNotifications.get(0);
        assertEquals(validRequest.email(), firstSave.getEmail());
        assertEquals(validRequest.message(), firstSave.getMessage());
        assertFalse(firstSave.getIsSent());

        Notification secondSave = capturedNotifications.get(1);
        assertEquals(validRequest.email(), secondSave.getEmail());
        assertEquals(validRequest.message(), secondSave.getMessage());
        assertTrue(secondSave.getIsSent());

        SimpleMailMessage capturedMessage = mailMessageCaptor.getValue();
        assertEquals(validRequest.email(), Objects.requireNonNull(capturedMessage.getTo())[0]);
        assertEquals("Уведомление от банковского приложения", capturedMessage.getSubject());
        assertEquals(validRequest.message(), capturedMessage.getText());
    }

    @Test
    void processNotification_WhenMailFails_ShouldNotMarkAsSent() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
        doThrow(new MailException("Failed to send") {}).when(mailSender).send(any(SimpleMailMessage.class));

        notificationService.processNotification(validRequest);

        verify(notificationRepository, times(1)).save(notificationCaptor.capture());
        Notification capturedNotification = notificationCaptor.getValue();
        assertFalse(capturedNotification.getIsSent());
    }

    @Test
    void retryFailedNotifications_ShouldProcessFailedNotifications() {
        List<Notification> failedNotifications = List.of(
            Notification.builder().email("test1@example.com").message("Message 1").isSent(false).build(),
            Notification.builder().email("test2@example.com").message("Message 2").isSent(false).build()
        );
        when(notificationRepository.findByIsSentFalse()).thenReturn(failedNotifications);

        notificationService.retryFailedNotifications();

        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
    }
} 