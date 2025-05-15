package com.cashservice.services;

import com.cashservice.dto.CashRequest;

public interface CashService {

    void processDeposit(CashRequest request);

    void processWithdraw(CashRequest request);
}
