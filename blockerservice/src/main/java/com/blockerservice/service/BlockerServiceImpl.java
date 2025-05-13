package com.blockerservice.service;

import com.blockerservice.dto.CashRequest;
import com.blockerservice.dto.SuspicionOperationResponse;
import com.blockerservice.dto.TransferRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class BlockerServiceImpl implements BlockerService {

    private static final Random RANDOM = new Random();

    @Override
    public SuspicionOperationResponse checkTransferOperation(TransferRequest request) {
        return new SuspicionOperationResponse(RANDOM.nextDouble() < 0.25);
    }

    @Override
    public SuspicionOperationResponse checkCashOperation(CashRequest request) {
        return new SuspicionOperationResponse(RANDOM.nextDouble() < 0.25);
    }

}
