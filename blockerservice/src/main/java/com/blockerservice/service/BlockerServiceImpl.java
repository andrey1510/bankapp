package com.blockerservice.service;

import com.blockerservice.dto.CashRequestDto;
import com.blockerservice.dto.SuspicionOperationDto;
import com.blockerservice.dto.TransferRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class BlockerServiceImpl implements BlockerService {

    private static final Random RANDOM = new Random();

    @Override
    public SuspicionOperationDto checkTransferOperation(TransferRequestDto request) {
        return new SuspicionOperationDto(RANDOM.nextDouble() < 0.25);
    }

    @Override
    public SuspicionOperationDto checkCashOperation(CashRequestDto request) {
        return new SuspicionOperationDto(RANDOM.nextDouble() < 0.25);
    }

}
