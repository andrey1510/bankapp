package com.blockerservice.service;

import com.blockerservice.dto.CashRequestDto;
import com.blockerservice.dto.SuspicionOperationDto;
import com.blockerservice.dto.TransferRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockerServiceImpl implements BlockerService {

    @Override
    public SuspicionOperationDto checkTransferOperation(TransferRequestDto request) {
        return new SuspicionOperationDto(request.amount() > 500);
    }

    @Override
    public SuspicionOperationDto checkCashOperation(CashRequestDto request) {
        return new SuspicionOperationDto(request.amount() > 1000);
    }

}
