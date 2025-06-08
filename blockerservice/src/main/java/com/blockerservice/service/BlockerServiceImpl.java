package com.blockerservice.service;

import com.blockerservice.dto.CashRequestDto;
import com.blockerservice.dto.SuspicionOperationDto;
import com.blockerservice.dto.TransferRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BlockerServiceImpl implements BlockerService {

    BigDecimal cashThreshold = new BigDecimal("500.00");
    BigDecimal transferThreshold = new BigDecimal("1000.00");

    @Override
    public SuspicionOperationDto checkTransferOperation(TransferRequestDto request) {
        return new SuspicionOperationDto(request.amount().compareTo(transferThreshold) > 0);
    }

    @Override
    public SuspicionOperationDto checkCashOperation(CashRequestDto request) {
        return new SuspicionOperationDto(request.amount().compareTo(cashThreshold) > 0);
    }

}
