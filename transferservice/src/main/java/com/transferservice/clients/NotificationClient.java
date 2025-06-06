package com.transferservice.clients;

import com.transferservice.dto.NotificationRequestDto;
import com.transferservice.dto.TransferRequestDto;
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

    public void sendBlockedTransferNotification(TransferRequestDto request) {
        String message = String.format("%s была заблокирована операция по переводу %.2f %s ",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
            request.amount(),
            request.senderAccountCurrency());

        restTemplate.postForObject(
            notificationServiceUrl,
            new NotificationRequestDto(request.email(), message),
            Void.class
        );
    }

}

