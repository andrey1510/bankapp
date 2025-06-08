package com.transferservice.clients;

import com.transferservice.dto.NotificationRequestDto;
import com.transferservice.dto.TransferRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class NotificationClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NotificationClient notificationClient;

    private TransferRequestDto transferRequest;

    @BeforeEach
    void setUp() {
        notificationClient.notificationServiceUrl = "http://notification-service";
        transferRequest = new TransferRequestDto(
            "user@example.com",
            1L,
            "USD",
            100.0,
            2L,
            "EUR"
        );
    }

    @Test
    void sendBlockedTransferNotification_ShouldCallCorrectEndpoint() {
        notificationClient.sendBlockedTransferNotification(transferRequest);

        verify(restTemplate).postForObject(
            eq("http://notification-service"),
            any(NotificationRequestDto.class),
            eq(Void.class)
        );
    }

    @Test
    void sendBlockedTransferNotification_ShouldCallService() {
        notificationClient.sendBlockedTransferNotification(transferRequest);

        verify(restTemplate).postForObject(
            eq("http://notification-service"),
            any(NotificationRequestDto.class),
            eq(Void.class)
        );
    }
}
