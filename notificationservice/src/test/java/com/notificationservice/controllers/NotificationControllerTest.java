package com.notificationservice.controllers;

import com.notificationservice.dto.NotificationRequestDto;
import com.notificationservice.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private NotificationController notificationController;

    private NotificationRequestDto validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new NotificationRequestDto("test@example.com", "Test message");

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void processNotification_ShouldReturnAccepted() {
        ResponseEntity<Void> response = notificationController.processNotification(validRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(notificationService).processNotification(validRequest);
    }
} 