package com.blockerservice.service;

import com.blockerservice.dto.CashRequestDto;
import com.blockerservice.dto.SuspicionOperationDto;
import com.blockerservice.dto.TransferRequestDto;

public interface BlockerService {
    SuspicionOperationDto checkTransferOperation(TransferRequestDto request);

    SuspicionOperationDto checkCashOperation(CashRequestDto request);
}
