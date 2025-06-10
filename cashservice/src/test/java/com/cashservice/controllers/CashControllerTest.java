package com.cashservice.controllers;

import com.cashservice.dto.CashRequestDto;
import com.cashservice.services.CashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CashControllerTest {

    @Mock
    private CashService cashService;

    @InjectMocks
    private CashController cashController;

    private CashRequestDto depositRequest;
    private CashRequestDto withdrawalRequest;

    @BeforeEach
    void setUp() {
        depositRequest = new CashRequestDto(
            "test@example.com",
            1L,
            "USD",
            new BigDecimal("100.00"),
            true
        );

        withdrawalRequest = new CashRequestDto(
            "test@example.com",
            1L,
            "USD",
            new BigDecimal("-100.00"),
            false
        );
    }

    @Test
    void processOperation_WithDepositRequest() {
        doNothing().when(cashService).processOperation(any(CashRequestDto.class));

        ResponseEntity<?> response = cashController.processOperation(depositRequest);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(cashService, times(1)).processOperation(depositRequest);
    }

    @Test
    void processOperation_WithWithdrawalRequest() {
        doNothing().when(cashService).processOperation(any(CashRequestDto.class));

        ResponseEntity<?> response = cashController.processOperation(withdrawalRequest);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(cashService, times(1)).processOperation(withdrawalRequest);
    }
} 