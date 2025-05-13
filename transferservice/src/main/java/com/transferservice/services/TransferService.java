package com.transferservice.services;

import com.transferservice.dto.TransferRequest;

public interface TransferService {
    void processTransfer(TransferRequest request);
}
