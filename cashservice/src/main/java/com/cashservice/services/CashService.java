package com.cashservice.services;

import com.cashservice.dto.CashIncomingRequest;

import java.util.UUID;

public interface CashService {

    void processDeposit(CashIncomingRequest request);

    void processWithdraw(CashIncomingRequest request);
}
