package com.blockerservice.services;

import com.blockerservice.dto.CashRequestDto;
import com.blockerservice.dto.SuspicionOperationDto;
import com.blockerservice.dto.TransferRequestDto;
import com.blockerservice.service.BlockerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class BlockerServiceImplTest {

    private BlockerServiceImpl blockerService;
    private TransferRequestDto typicalTransferRequest;
    private CashRequestDto typicalCashRequest;

    @BeforeEach
    void setUp() {
        blockerService = new BlockerServiceImpl();

        typicalTransferRequest = new TransferRequestDto(
            "user@example.com",
            12345L,
            "USD",
            new BigDecimal("300.00"),
            67890L,
            "EUR",
            "login",
            "login"
        );

        typicalCashRequest = new CashRequestDto(
            "user@example.com",
            12345L,
            "USD",
            new BigDecimal("800.00"),
            "login"
        );
    }

    @Test
    void checkTransferOperation_ShouldReturnNotSuspicious_WhenAmountBelowThreshold() {

        SuspicionOperationDto result = blockerService.checkTransferOperation(typicalTransferRequest);

        assertFalse(result.isSuspicious());
    }

    @Test
    void checkTransferOperation_ShouldNotReturnSuspicious_WhenAmountBelowThreshold() {

        TransferRequestDto suspiciousRequest = new TransferRequestDto(
            typicalTransferRequest.email(),
            typicalTransferRequest.senderAccountId(),
            typicalTransferRequest.senderAccountCurrency(),
            new BigDecimal("505.01"),
            typicalTransferRequest.recipientAccountId(),
            typicalTransferRequest.recipientAccountCurrency(),
            "login",
            "login"
        );

        SuspicionOperationDto result = blockerService.checkTransferOperation(suspiciousRequest);

        assertFalse(result.isSuspicious());
    }

    @Test
    void checkCashOperation_ShouldReturnNotSuspicious_WhenAmountBelowThreshold() {

        SuspicionOperationDto result = blockerService.checkCashOperation(typicalCashRequest);

        assertTrue(result.isSuspicious());
    }

    @Test
    void checkCashOperation_ShouldReturnSuspicious_WhenAmountAboveThreshold() {

        CashRequestDto suspiciousRequest = new CashRequestDto(
            typicalCashRequest.email(),
            typicalCashRequest.accountId(),
            typicalCashRequest.currency(),
            new BigDecimal("1011.50"),
            "login"
        );

        SuspicionOperationDto result = blockerService.checkCashOperation(suspiciousRequest);

        assertTrue(result.isSuspicious());
    }

    @Test
    void checkTransferOperation_ShouldHandleEdgeCases() {

        TransferRequestDto edgeCaseRequest = new TransferRequestDto(
            typicalTransferRequest.email(),
            typicalTransferRequest.senderAccountId(),
            typicalTransferRequest.senderAccountCurrency(),
            new BigDecimal("500.00"),
            typicalTransferRequest.recipientAccountId(),
            typicalTransferRequest.recipientAccountCurrency(),
            "login",
            "login"
        );

        assertFalse(blockerService.checkTransferOperation(edgeCaseRequest).isSuspicious());

        TransferRequestDto aboveEdgeRequest = new TransferRequestDto(
            typicalTransferRequest.email(),
            typicalTransferRequest.senderAccountId(),
            typicalTransferRequest.senderAccountCurrency(),
            new BigDecimal("505.01"),
            typicalTransferRequest.recipientAccountId(),
            typicalTransferRequest.recipientAccountCurrency(),
            "login",
            "login"
        );

        assertFalse(blockerService.checkTransferOperation(aboveEdgeRequest).isSuspicious());
    }

    @Test
    void checkCashOperation_ShouldHandleEdgeCases() {
        CashRequestDto edgeCaseRequest = new CashRequestDto(
            typicalCashRequest.email(),
            typicalCashRequest.accountId(),
            typicalCashRequest.currency(),
            new BigDecimal("1000.0"),
            "login"
        );

        assertTrue(blockerService.checkCashOperation(edgeCaseRequest).isSuspicious());

        CashRequestDto aboveEdgeRequest = new CashRequestDto(
            typicalCashRequest.email(),
            typicalCashRequest.accountId(),
            typicalCashRequest.currency(),
            new BigDecimal("1001.00"),
            "login"
        );

        assertTrue(blockerService.checkCashOperation(aboveEdgeRequest).isSuspicious());
    }
}
