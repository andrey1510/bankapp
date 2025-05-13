package com.blockerservice.service;

import com.blockerservice.dto.CashRequest;
import com.blockerservice.dto.SuspicionOperationResponse;
import com.blockerservice.dto.TransferRequest;

public interface BlockerService {
    SuspicionOperationResponse checkTransferOperation(TransferRequest request);

    SuspicionOperationResponse checkCashOperation(CashRequest request);
}
