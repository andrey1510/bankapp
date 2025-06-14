package com.cashservice.clients;

import com.cashservice.dto.CashRequestDto;
import com.cashservice.dto.NotificationRequestDto;
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
public class NotificationClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NotificationClient notificationClient;

    private CashRequestDto testRequest;
    private String expectedMessage;

    @BeforeEach
    void setUp() {
        notificationClient.notificationServiceUrl = "http://notification-service";
        testRequest = new CashRequestDto(
            "test@email.com", 1L, "USD", new BigDecimal("100.00"), true);

    }
    @Test
    void sendBlockedCashNotification_shouldCallRestTemplate() {
        notificationClient.sendBlockedCashNotification(testRequest);
        verify(restTemplate).postForObject(
            eq("http://notification-service/notifications"),
            any(NotificationRequestDto.class),
            eq(Void.class)
        );
    }


}
