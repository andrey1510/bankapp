package com.cashservice.clients;

import com.cashservice.dto.CashRequest;
import com.cashservice.dto.NotificationRequest;
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

    public void sendBlockedCashNotification(CashRequest request, boolean isDeposit) {
        String operationType = isDeposit ? "пополнению" : "снятию";
        String message = String.format("%s была заблокирована операция по %s счета %s на сумму %.2f %s",
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            operationType,
            request.accountId(),
            request.amount(),
            request.currency().toUpperCase());

        restTemplate.postForObject(
            notificationServiceUrl + "/api/notifications",
            new NotificationRequest(request.email(), message),
            Void.class
        );
    }
}
