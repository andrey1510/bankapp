package com.blockerservice.service;

import com.blockerservice.dto.CashRequestDto;
import com.blockerservice.dto.SuspicionOperationDto;
import com.blockerservice.dto.TransferRequestDto;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockerServiceImpl implements BlockerService {

    private final MeterRegistry meterRegistry;

    BigDecimal cashThreshold = new BigDecimal("500.00");
    BigDecimal transferThreshold = new BigDecimal("1000.00");

    @Override
    public SuspicionOperationDto checkTransferOperation(TransferRequestDto request) {
        SuspicionOperationDto suspicionOperationDto = new SuspicionOperationDto(request.amount().compareTo(transferThreshold) > 0);
        log.info("Suspicion operation: {}", suspicionOperationDto);

        if(suspicionOperationDto.isSuspicious()) {
            meterRegistry.counter("transfer_blocked",
                "sender_login", request.senderLogin(),
                "recipient_login", request.recipientLogin(),
                "sender_account", request.senderAccountId().toString(),
                "recipient_account", request.recipientAccountId().toString()
            ).increment();
        }

        return suspicionOperationDto;
    }

    @Override
    public SuspicionOperationDto checkCashOperation(CashRequestDto request) {
        SuspicionOperationDto suspicionOperationDto = new SuspicionOperationDto(request.amount().compareTo(cashThreshold) > 0);
        log.info("Suspicion operation: {}", suspicionOperationDto);

        if(suspicionOperationDto.isSuspicious()) {
            meterRegistry.counter("cash_blocked",
                "login", request.login(),
                "account", request.accountId().toString()
            ).increment();
        }
        return suspicionOperationDto;
    }

}
