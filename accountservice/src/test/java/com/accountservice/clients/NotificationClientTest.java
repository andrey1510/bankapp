package com.accountservice.clients;

import com.accountservice.dto.NotificationRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NotificationClient notificationClient;

    private final String testEmail = "user@example.com";
    private final String testCurrency = "USD";

    @BeforeEach
    void setUp() {
        notificationClient.notificationServiceUrl = "http://notification-service";
    }

    @Test
    void sendCashNotification_ShouldCallNotificationService() {

        notificationClient.sendCashNotification(new BigDecimal("100.00"), testCurrency, testEmail);

        verify(restTemplate).postForObject(
            eq("http://notification-service"),
            any(NotificationRequestDto.class),
            eq(Void.class)
        );
    }

    @Test
    void sendCashNotification_WithNegativeAmount_ShouldCallNotificationService() {

        notificationClient.sendCashNotification(new BigDecimal("-50.00"), testCurrency, testEmail);

        verify(restTemplate).postForObject(
            eq("http://notification-service"),
            any(NotificationRequestDto.class),
            eq(Void.class)
        );
    }

    @Test
    void sendTransferNotification_ShouldCallNotificationService() {

        notificationClient.sendTransferNotification(new BigDecimal("200.00"), testCurrency, testEmail);

        verify(restTemplate).postForObject(
            eq("http://notification-service"),
            any(NotificationRequestDto.class),
            eq(Void.class)
        );
    }
}