package com.cashservice.clients;

import com.cashservice.dto.CashRequestDto;
import com.cashservice.dto.NotificationRequestDto;
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

    public void sendBlockedCashNotification(CashRequestDto request) {
        String operationType = request.isDeposit() ? "пополнению" : "снятию со";
        String message = String.format("%s была заблокирована операция по %s счета на сумму %.2f %s",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
            operationType,
            request.amount(),
            request.currency());

        restTemplate.postForObject(
            notificationServiceUrl,
            new NotificationRequestDto(request.email(), message),
            Void.class
        );
    }

    public void sendCashNotification(CashRequestDto request) {
        String operationType = request.isDeposit() ? "пополнению" : "снятию со";
        String message = String.format("%s была проведена операция по %s счета на сумму %.2f %s",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
            operationType,
            request.amount(),
            request.currency());

        restTemplate.postForObject(
            notificationServiceUrl,
            new NotificationRequestDto(request.email(), message),
            Void.class
        );
    }
}
