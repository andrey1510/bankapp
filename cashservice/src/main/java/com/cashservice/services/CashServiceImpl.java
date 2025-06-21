package com.cashservice.services;

import com.cashservice.clients.AccountClient;
import com.cashservice.clients.BlockerClient;
import com.cashservice.dto.CashRequestDto;
import com.cashservice.dto.kafka.NotificationRequestDto;
import com.cashservice.dto.SuspicionOperationDto;
import com.cashservice.exceptions.CashOperationException;
import com.cashservice.kafka.NotificationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashServiceImpl implements CashService {

    private final AccountClient accountClient;
    private final BlockerClient blockerClient;
    private final NotificationProducer notificationProducer;

    @Override
    public void processOperation(CashRequestDto request) {

        SuspicionOperationDto response = blockerClient.checkCashOperation(request);

        if (response != null && response.isSuspicious()) {
            notificationProducer.sendNotification(new NotificationRequestDto(request.email(), createMessage(request)));
            throw new CashOperationException("Операция заблокирована");
        }

        BigDecimal amount = request.amount();
        if (!request.isDeposit()) amount = amount.negate().setScale(2, RoundingMode.HALF_UP);

        try {
            accountClient.sendAccountRequest(request.accountId(), amount, request.login());
        } catch (RestClientException e) {
            handleAccountServiceError(e);
        }

    }

    private String createMessage(CashRequestDto request) {
        return String.format("Пользователю %s %s была заблокирована операция по %s счета на сумму %.2f %s",
            request.login(),
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
            request.isDeposit() ? "пополнению" : "снятию со",
            request.amount().setScale(2, RoundingMode.HALF_UP).doubleValue(),
            request.currency());
    }

    private void handleAccountServiceError(RestClientException e) {
        if (e instanceof HttpClientErrorException) {
            HttpClientErrorException httpEx = (HttpClientErrorException) e;

            if (httpEx.getStatusCode() == HttpStatus.BAD_REQUEST) {
                String errorMessage = extractErrorMessage(httpEx);
                throw new CashOperationException(errorMessage);
            }
        }

        throw new CashOperationException("Ошибка: " + e.getMessage());
    }

    private String extractErrorMessage(HttpClientErrorException ex) {
        try {
            return ex.getResponseBodyAsString();
        } catch (Exception e) {
            return "Недостаточно средств";
        }
    }

}
