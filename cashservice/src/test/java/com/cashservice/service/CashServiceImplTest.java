package com.cashservice.service;

import com.cashservice.clients.AccountClient;
import com.cashservice.clients.BlockerClient;
import com.cashservice.dto.CashRequestDto;
import com.cashservice.dto.SuspicionOperationDto;
import com.cashservice.services.CashServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CashServiceImplTest {

    @Mock
    private AccountClient accountClient;

    @Mock
    private BlockerClient blockerClient;

    @InjectMocks
    private CashServiceImpl cashService;

    private CashRequestDto validRequest;
    private CashRequestDto depositRequest;
    private CashRequestDto withdrawRequest;

    @BeforeEach
    void setUp() {
        depositRequest = new CashRequestDto(
            "test@example.com",
            1L,
            "USD",
            new BigDecimal("100.00"),
            true,
            "login"
        );
        withdrawRequest = new CashRequestDto(
            "test@example.com",
            1L,
            "USD",
            new BigDecimal("50.00"),
            false,
            "login"
        );
        validRequest = depositRequest;
    }

    @Test
    void processOperation_shouldProcessDepositWhenNotSuspicious() {
        when(blockerClient.checkCashOperation(any())).thenReturn(new SuspicionOperationDto(false));

        assertDoesNotThrow(() -> cashService.processOperation(depositRequest));
        verify(accountClient).sendAccountRequest(eq(1L), eq(new BigDecimal("100.00")), eq("login"));
    }

    @Test
    void processOperation_shouldProcessWithdrawWhenNotSuspicious() {
        when(blockerClient.checkCashOperation(any())).thenReturn(new SuspicionOperationDto(false));

        assertDoesNotThrow(() -> cashService.processOperation(withdrawRequest));
        verify(accountClient).sendAccountRequest(eq(1L), eq(new BigDecimal("-50.00")), eq("login"));
    }

}