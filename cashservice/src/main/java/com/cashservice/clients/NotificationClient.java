package com.cashservice.clients;

import com.cashservice.dto.CashRequestDto;
import com.cashservice.dto.NotificationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Value("${notificationservice.url}")
    protected String notificationServiceUrl;

    @Qualifier("notificationRestTemplate")
    private final RestTemplate restTemplate;

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 2, backoff = @Backoff(delay = 1000)
    )
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

}
