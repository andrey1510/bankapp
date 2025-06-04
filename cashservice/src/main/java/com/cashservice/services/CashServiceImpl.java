package com.cashservice.services;

import com.cashservice.clients.AccountClient;
import com.cashservice.clients.BlockerClient;
import com.cashservice.clients.NotificationClient;
import com.cashservice.dto.CashRequestDto;
import com.cashservice.dto.SuspicionOperationDto;
import com.cashservice.exceptions.CashOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashServiceImpl implements CashService {

    private final AccountClient accountClient;
    private final BlockerClient blockerClient;
    private final NotificationClient notificationClient;

    @Override
    public void processOperation(CashRequestDto request) {

        SuspicionOperationDto response = blockerClient.checkCashOperation(request);

        if (response != null && response.isSuspicious()) {
            notificationClient.sendBlockedCashNotification(request);
            log.info("Операция заблокирована");
            throw new CashOperationException("Операция заблокирована");
        }

        Double amount = request.amount();
        if (!request.isDeposit()) amount = -amount;

        try {
            accountClient.sendAccountRequest(request.accountId(), amount);
        } catch (RestClientException e) {
            handleAccountServiceError(e);
        }

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
