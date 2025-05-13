package com.cashservice.services;

import com.cashservice.clients.AccountClient;
import com.cashservice.clients.BlockerClient;
import com.cashservice.dto.CashIncomingRequest;
import com.cashservice.dto.SuspicionOperationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashServiceImpl implements CashService {

    private final AccountClient accountClient;
    private final BlockerClient blockerClient;

    @Override
    public void processDeposit(CashIncomingRequest request) {

        SuspicionOperationResponse response = blockerClient.checkCashOperation(request);
        if (response != null && response.isSuspicious()) throw new IllegalStateException("Операция заблокирована");

        accountClient.deposit(request.accountId(), request.amount());
    }

    @Override
    public void processWithdraw(CashIncomingRequest request) {

        SuspicionOperationResponse response = blockerClient.checkCashOperation(request);
        if (response != null && response.isSuspicious()) throw new IllegalStateException("Операция заблокирована");

        accountClient.withdraw(request.accountId(), request.amount());
    }

}
