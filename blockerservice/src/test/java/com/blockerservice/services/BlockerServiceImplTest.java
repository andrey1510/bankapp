package com.blockerservice.services;

import com.blockerservice.dto.CashRequestDto;
import com.blockerservice.dto.SuspicionOperationDto;
import com.blockerservice.dto.TransferRequestDto;
import com.blockerservice.service.BlockerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

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
            300.0,
            67890L,
            "EUR"
        );

        typicalCashRequest = new CashRequestDto(
            "user@example.com",
            12345L,
            "USD",
            800.0
        );
    }

    @Test
    void checkTransferOperation_ShouldReturnNotSuspicious_WhenAmountBelowThreshold() {

        SuspicionOperationDto result = blockerService.checkTransferOperation(typicalTransferRequest);

        assertFalse(result.isSuspicious());
    }

    @Test
    void checkTransferOperation_ShouldReturnSuspicious_WhenAmountAboveThreshold() {

        TransferRequestDto suspiciousRequest = new TransferRequestDto(
            typicalTransferRequest.email(),
            typicalTransferRequest.senderAccountId(),
            typicalTransferRequest.senderAccountCurrency(),
            505.0,
            typicalTransferRequest.recipientAccountId(),
            typicalTransferRequest.recipientAccountCurrency()
        );

        SuspicionOperationDto result = blockerService.checkTransferOperation(suspiciousRequest);

        assertTrue(result.isSuspicious());
    }

    @Test
    void checkCashOperation_ShouldReturnNotSuspicious_WhenAmountBelowThreshold() {

        SuspicionOperationDto result = blockerService.checkCashOperation(typicalCashRequest);

        assertFalse(result.isSuspicious());
    }

    @Test
    void checkCashOperation_ShouldReturnSuspicious_WhenAmountAboveThreshold() {

        CashRequestDto suspiciousRequest = new CashRequestDto(
            typicalCashRequest.email(),
            typicalCashRequest.accountId(),
            typicalCashRequest.currency(),
            1011.0
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
            500.0,
            typicalTransferRequest.recipientAccountId(),
            typicalTransferRequest.recipientAccountCurrency()
        );

        assertFalse(blockerService.checkTransferOperation(edgeCaseRequest).isSuspicious());

        TransferRequestDto aboveEdgeRequest = new TransferRequestDto(
            typicalTransferRequest.email(),
            typicalTransferRequest.senderAccountId(),
            typicalTransferRequest.senderAccountCurrency(),
            505.01,
            typicalTransferRequest.recipientAccountId(),
            typicalTransferRequest.recipientAccountCurrency()
        );

        assertTrue(blockerService.checkTransferOperation(aboveEdgeRequest).isSuspicious());
    }

    @Test
    void checkCashOperation_ShouldHandleEdgeCases() {
        CashRequestDto edgeCaseRequest = new CashRequestDto(
            typicalCashRequest.email(),
            typicalCashRequest.accountId(),
            typicalCashRequest.currency(),
            1000.0
        );

        assertFalse(blockerService.checkCashOperation(edgeCaseRequest).isSuspicious());

        CashRequestDto aboveEdgeRequest = new CashRequestDto(
            typicalCashRequest.email(),
            typicalCashRequest.accountId(),
            typicalCashRequest.currency(),
            1000.01
        );

        assertTrue(blockerService.checkCashOperation(aboveEdgeRequest).isSuspicious());
    }
}
