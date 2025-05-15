package com.blockerservice.service;

import com.blockerservice.dto.CashRequest;
import com.blockerservice.dto.SuspicionOperation;
import com.blockerservice.dto.TransferRequest;

public interface BlockerService {
    SuspicionOperation checkTransferOperation(TransferRequest request);

    SuspicionOperation checkCashOperation(CashRequest request);
}
