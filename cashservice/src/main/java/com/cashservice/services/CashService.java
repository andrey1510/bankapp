package com.cashservice.services;

import com.cashservice.dto.CashRequestDto;

public interface CashService {

    void processDeposit(CashRequestDto request);

    void processWithdraw(CashRequestDto request);
}
