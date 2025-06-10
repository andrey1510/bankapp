package com.blockerservice.controller;

import com.blockerservice.dto.CashRequestDto;
import com.blockerservice.dto.SuspicionOperationDto;
import com.blockerservice.dto.TransferRequestDto;
import com.blockerservice.service.BlockerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlockerControllerTest {

    @Mock
    private BlockerService blockerService;

    @InjectMocks
    private BlockerController blockerController;

    private TransferRequestDto transferRequest;
    private CashRequestDto cashRequest;
    private SuspicionOperationDto suspicionOperation;

    @BeforeEach
    void setUp() {
        transferRequest = new TransferRequestDto(
            "test@example.com",
            1L,
            "USD",
            new BigDecimal("100.00"),
            2L,
            "EUR"
        );

        cashRequest = new CashRequestDto(
            "test@example.com",
            1L,
            "USD",
            new BigDecimal("100.00")
        );

        suspicionOperation = new SuspicionOperationDto(
            true
        );
    }

    @Test
    void checkTransferOperation_ShouldReturnSuspicionOperation() {
        when(blockerService.checkTransferOperation(any(TransferRequestDto.class)))
            .thenReturn(suspicionOperation);

        SuspicionOperationDto result = blockerController.checkTransferOperation(transferRequest);

        assertEquals(suspicionOperation, result);
        verify(blockerService, times(1)).checkTransferOperation(transferRequest);
    }

    @Test
    void checkCashOperation_ShouldReturnSuspicionOperation() {
        when(blockerService.checkCashOperation(any(CashRequestDto.class)))
            .thenReturn(suspicionOperation);

        SuspicionOperationDto result = blockerController.checkCashOperation(cashRequest);

        assertEquals(suspicionOperation, result);
        verify(blockerService, times(1)).checkCashOperation(cashRequest);
    }
} 