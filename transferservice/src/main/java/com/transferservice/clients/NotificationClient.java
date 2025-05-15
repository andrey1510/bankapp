package com.transferservice.clients;

import com.transferservice.dto.NotificationRequest;
import com.transferservice.dto.TransferRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class NotificationClient {

    private final RestTemplate restTemplate;

    @Value("${notificationservice.url}")
    private String notificationServiceUrl;

    public void sendBlockedTransferNotification(TransferRequest request) {
        String message = String.format("%s была заблокирована операция по переводу %.2f %s со счета %s на счет %s",
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            request.amount(),
            request.senderAccountCurrency().toUpperCase(),
            request.senderAccountId(),
            request.recipientAccountId());

        restTemplate.postForObject(
            notificationServiceUrl + "/api/notifications",
            new NotificationRequest(request.email(), message),
            Void.class
        );
    }
}

