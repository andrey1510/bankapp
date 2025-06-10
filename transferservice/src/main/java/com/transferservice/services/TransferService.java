package com.transferservice.services;

import com.transferservice.dto.TransferRequestDto;

public interface TransferService {
    void processTransfer(TransferRequestDto request);
}
