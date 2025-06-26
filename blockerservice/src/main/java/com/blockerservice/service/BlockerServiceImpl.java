package com.blockerservice.service;

import com.blockerservice.dto.CashRequestDto;
import com.blockerservice.dto.SuspicionOperationDto;
import com.blockerservice.dto.TransferRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockerServiceImpl implements BlockerService {

    BigDecimal cashThreshold = new BigDecimal("500.00");
    BigDecimal transferThreshold = new BigDecimal("1000.00");

    @Override
    public SuspicionOperationDto checkTransferOperation(TransferRequestDto request) {
        SuspicionOperationDto suspicionOperationDto = new SuspicionOperationDto(request.amount().compareTo(transferThreshold) > 0);
        log.info("Suspicion operation: {}", suspicionOperationDto);
        return suspicionOperationDto;
    }

    @Override
    public SuspicionOperationDto checkCashOperation(CashRequestDto request) {
        SuspicionOperationDto suspicionOperationDto = new SuspicionOperationDto(request.amount().compareTo(cashThreshold) > 0);
        log.info("Suspicion operation: {}", suspicionOperationDto);
        return suspicionOperationDto;
    }

}
