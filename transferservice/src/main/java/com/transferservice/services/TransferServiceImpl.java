package com.transferservice.services;

import com.transferservice.clients.AccountClient;
import com.transferservice.clients.BlockerClient;
import com.transferservice.clients.ExchangeClient;
import com.transferservice.clients.NotificationClient;
import com.transferservice.dto.BalanceUpdateRequestDto;
import com.transferservice.dto.ConversionRateRequestDto;
import com.transferservice.dto.SuspicionOperationDto;
import com.transferservice.dto.TransferRequestDto;
import com.transferservice.exceptions.SameAccountTransferException;
import com.transferservice.exceptions.TransferOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountClient accountClient;
    private final BlockerClient blockerClient;
    private final ExchangeClient exchangeClient;
    private final NotificationClient notificationClient;

    @Override
    public void processTransfer(TransferRequestDto request) {

        if (Objects.equals(request.senderAccountId(), request.recipientAccountId()))
            throw new SameAccountTransferException("Счета должны быть разными.");

        BigDecimal conversionRate = BigDecimal.ONE;

        if (!request.senderAccountCurrency().equalsIgnoreCase(request.recipientAccountCurrency())) {
            conversionRate = exchangeClient.getConversionRate(new ConversionRateRequestDto(
                request.senderAccountCurrency(),
                request.recipientAccountCurrency()
            )).rate();
        }

        BalanceUpdateRequestDto updateRequest = new BalanceUpdateRequestDto(
            request.senderAccountId(),
            request.amount(),
            request.recipientAccountId(),
            request.amount()
                .multiply(conversionRate)
                .setScale(2, RoundingMode.HALF_UP)
        );

        SuspicionOperationDto response = blockerClient.checkTransferOperation(request);
        if (response != null && response.isSuspicious()) {
            notificationClient.sendBlockedTransferNotification(request);
            throw new TransferOperationException("Операция заблокирована");
        }

        try {
            accountClient.updateBalances(updateRequest);
        } catch (
            RestClientException e) {
              handleAccountServiceError(e);
        }
    }

    private void handleAccountServiceError(RestClientException e) {
        if (e instanceof HttpClientErrorException) {
            HttpClientErrorException httpEx = (HttpClientErrorException) e;

            if (httpEx.getStatusCode() == HttpStatus.BAD_REQUEST) {
                String errorMessage = extractErrorMessage(httpEx);
                throw new TransferOperationException(errorMessage);
            }
        }

        throw new TransferOperationException("Ошибка: " + e.getMessage());
    }

    private String extractErrorMessage(HttpClientErrorException ex) {
        try {
            return ex.getResponseBodyAsString();
        } catch (Exception e) {
            return "Недостаточно средств";
        }
    }

}
