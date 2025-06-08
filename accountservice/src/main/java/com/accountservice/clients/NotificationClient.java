package com.accountservice.clients;

import com.accountservice.dto.NotificationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class NotificationClient {

    private final RestTemplate restTemplate;

    @Value("${notificationservice.url}")
    private String notificationServiceUrl;

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 2, backoff = @Backoff(delay = 1000)
    )
    public void sendCashNotification(Double amount, String currency, String email) {

        String operationType = "пополнению";

        if(amount < 0) {
            amount = -amount;
            operationType = "снятию со";
        }

        String message = String.format("%s была проведена операция по %s счета на сумму %.2f %s",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
            operationType,
            amount,
            currency);

        restTemplate.postForObject(
            notificationServiceUrl,
            new NotificationRequestDto(email, message),
            Void.class
        );
    }

    public void sendTransferNotification(Double amount, String currency, String email) {
        String message = String.format("%s была проведена операция по переводу %.2f %s ",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
            amount,
            currency);

        restTemplate.postForObject(
            notificationServiceUrl,
            new NotificationRequestDto(email, message),
            Void.class
        );
    }

}
