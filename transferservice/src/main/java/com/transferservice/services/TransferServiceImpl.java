package com.transferservice.services;

import com.transferservice.clients.AccountClient;
import com.transferservice.clients.BlockerClient;
import com.transferservice.clients.ExchangeClient;
import com.transferservice.clients.NotificationClient;
import com.transferservice.dto.BalanceUpdateRequest;
import com.transferservice.dto.SuspicionOperation;
import com.transferservice.dto.TransferRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountClient accountClient;
    private final BlockerClient blockerClient;
    private final ExchangeClient exchangeClient;
    private final NotificationClient notificationClient;

    @Override
    public void processTransfer(TransferRequest request) {

        SuspicionOperation response = blockerClient.checkTransferOperation(request);
        if (response != null && response.isSuspicious()) {
            notificationClient.sendBlockedTransferNotification(request);
            throw new IllegalStateException("Операция заблокирована");
        }

        Double conversionRate = 1.0;

        if (!request.senderAccountCurrency().equalsIgnoreCase(request.recipientAccountCurrency())) {
            conversionRate = exchangeClient.getConversionRate(
                request.senderAccountCurrency(),
                request.recipientAccountCurrency()
            );
        }

        BalanceUpdateRequest updateRequest = new BalanceUpdateRequest(
            request.senderAccountId(),
            request.amount(),
            request.recipientAccountId(),
            Math.round(request.amount() * conversionRate * 100.0) / 100.0
        );

        accountClient.updateBalances(updateRequest);
    }

}
