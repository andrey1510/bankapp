package com.blockerservice.service;

import com.blockerservice.dto.CashRequest;
import com.blockerservice.dto.SuspicionOperation;
import com.blockerservice.dto.TransferRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class BlockerServiceImpl implements BlockerService {

    private static final Random RANDOM = new Random();

    @Override
    public SuspicionOperation checkTransferOperation(TransferRequest request) {
        return new SuspicionOperation(RANDOM.nextDouble() < 0.25);
    }

    @Override
    public SuspicionOperation checkCashOperation(CashRequest request) {
        return new SuspicionOperation(RANDOM.nextDouble() < 0.25);
    }

}
