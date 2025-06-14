package com.cashservice.clients;

import com.cashservice.dto.CashRequestDto;
import com.cashservice.dto.SuspicionOperationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BlockerClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BlockerClient blockerClient;

    private CashRequestDto testRequest;
    private SuspicionOperationDto testResponse;

    @BeforeEach
    void setUp() {
        blockerClient.blockerServiceUrl = "http://blocker-service";
        testRequest = new CashRequestDto(
            "test@email.com", 1L, "USD", new BigDecimal("100.00"), true);
        testResponse = new SuspicionOperationDto(false);
    }

    @Test
    void checkCashOperation_shouldCallCorrectEndpoint() {
        when(restTemplate.postForObject(anyString(), any(), eq(SuspicionOperationDto.class)))
            .thenReturn(testResponse);

        SuspicionOperationDto result = blockerClient.checkCashOperation(testRequest);

        verify(restTemplate).postForObject(
            eq("http://blocker-service/fraud-check/cash"),
            eq(testRequest),
            eq(SuspicionOperationDto.class)
        );
        assertEquals(testResponse, result);
    }

    @Test
    void checkCashOperation_shouldReturnSuspiciousResponse() {
        SuspicionOperationDto suspiciousResponse = new SuspicionOperationDto(true);
        when(restTemplate.postForObject(anyString(), any(), any()))
            .thenReturn(suspiciousResponse);

        SuspicionOperationDto result = blockerClient.checkCashOperation(testRequest);

        assertTrue(result.isSuspicious());
    }

    @Test
    void checkCashOperation_shouldReturnNotSuspiciousResponse() {
        when(restTemplate.postForObject(anyString(), any(), any()))
            .thenReturn(testResponse);

        SuspicionOperationDto result = blockerClient.checkCashOperation(testRequest);

        assertFalse(result.isSuspicious());
    }
}
