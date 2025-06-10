package com.cashservice.services;

import com.cashservice.dto.CashRequestDto;

public interface CashService {

    void processOperation(CashRequestDto request);

}
