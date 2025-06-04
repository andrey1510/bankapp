package com.cashservice.services;

import com.cashservice.clients.AccountClient;
import com.cashservice.clients.BlockerClient;
import com.cashservice.clients.NotificationClient;
import com.cashservice.dto.CashRequestDto;
import com.cashservice.dto.SuspicionOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashServiceImpl implements CashService {

    private final AccountClient accountClient;
    private final BlockerClient blockerClient;
    private final NotificationClient notificationClient;

    @Override
    public void processDeposit(CashRequestDto request) {

        SuspicionOperation response = blockerClient.checkCashOperation(request);
        if (response != null && response.isSuspicious()) {
            notificationClient.sendBlockedCashNotification(request, true);
            throw new IllegalStateException("Операция заблокирована");
        }

        accountClient.deposit(request.accountId(), request.amount());
    }

    @Override
    public void processWithdraw(CashRequestDto request) {

        SuspicionOperation response = blockerClient.checkCashOperation(request);
        if (response != null && response.isSuspicious()) {
            notificationClient.sendBlockedCashNotification(request, false);
            throw new IllegalStateException("Операция заблокирована");
        }

        accountClient.withdraw(request.accountId(), request.amount());
    }

}
