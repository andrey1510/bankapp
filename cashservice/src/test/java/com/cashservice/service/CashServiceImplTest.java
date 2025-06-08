package com.cashservice.service;

import com.cashservice.clients.AccountClient;
import com.cashservice.clients.BlockerClient;
import com.cashservice.clients.NotificationClient;
import com.cashservice.dto.CashRequestDto;
import com.cashservice.dto.SuspicionOperationDto;
import com.cashservice.exceptions.CashOperationException;
import com.cashservice.services.CashServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CashServiceImplTest {

    @Mock
    private AccountClient accountClient;

    @Mock
    private BlockerClient blockerClient;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private CashServiceImpl cashService;

    private CashRequestDto validRequest;
    private CashRequestDto depositRequest;
    private CashRequestDto withdrawRequest;

    @BeforeEach
    void setUp() {
        depositRequest = new CashRequestDto("test@example.com", 1L, "USD", 100.0, true);
        withdrawRequest = new CashRequestDto("test@example.com", 1L, "USD", 50.0, false);
        validRequest = depositRequest;
    }

    @Test
    void processOperation_shouldProcessDepositWhenNotSuspicious() {
        when(blockerClient.checkCashOperation(any())).thenReturn(new SuspicionOperationDto(false));

        assertDoesNotThrow(() -> cashService.processOperation(depositRequest));
        verify(accountClient).sendAccountRequest(eq(1L), eq(100.0));
    }

    @Test
    void processOperation_shouldProcessWithdrawWhenNotSuspicious() {
        when(blockerClient.checkCashOperation(any())).thenReturn(new SuspicionOperationDto(false));

        assertDoesNotThrow(() -> cashService.processOperation(withdrawRequest));
        verify(accountClient).sendAccountRequest(eq(1L), eq(-50.0));
    }

    @Test
    void processOperation_shouldBlockWhenSuspicious() {
        when(blockerClient.checkCashOperation(any())).thenReturn(new SuspicionOperationDto(true));

        assertThrows(CashOperationException.class, () -> cashService.processOperation(validRequest));
        verify(notificationClient).sendBlockedCashNotification(any());
        verifyNoInteractions(accountClient);
    }

}