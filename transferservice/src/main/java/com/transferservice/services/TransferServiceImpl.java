package com.transferservice.services;

import com.transferservice.clients.AccountClient;
import com.transferservice.clients.BlockerClient;
import com.transferservice.clients.ExchangeClient;
import com.transferservice.dto.BalanceUpdateRequest;
import com.transferservice.dto.SuspicionOperationResponse;
import com.transferservice.dto.TransferRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountClient accountClient;
    private final BlockerClient blockerClient;
    private final ExchangeClient exchangeClient;

    @Override
    public void processTransfer(TransferRequest request) {

        SuspicionOperationResponse response = blockerClient.checkTransferOperation(request);
        if (response != null && response.isSuspicious()) throw new IllegalStateException("Операция заблокирована");

        Double conversionRate = 1.0;

        if (!request.senderAccountCurrency().equalsIgnoreCase(request.recipientAccountCurrency())) {
            conversionRate = exchangeClient.getConversionRate(
                request.senderAccountCurrency(),
                request.recipientAccountCurrency()
            );
        }

        Double recipientAmount = Math.round(request.amount() * conversionRate * 100.0) / 100.0;

        BalanceUpdateRequest updateRequest = new BalanceUpdateRequest(
            request.senderAccountId(),
            request.amount(),
            request.recipientAccountId(),
            recipientAmount
        );

        accountClient.updateBalances(updateRequest);
    }

}
