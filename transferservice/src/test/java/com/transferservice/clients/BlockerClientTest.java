package com.transferservice.clients;

import com.transferservice.dto.SuspicionOperationDto;
import com.transferservice.dto.TransferRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlockerClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BlockerClient blockerClient;

    private TransferRequestDto transferRequest;
    private SuspicionOperationDto suspicionResponse;

    @BeforeEach
    void setUp() {
        blockerClient.blockerUrl = "http://blocker-service";
        transferRequest = new TransferRequestDto(
            "user@example.com",
            1L,
            "USD",
            new BigDecimal("100.00"),
            2L,
            "EUR",
            "login1",
            "login2"
        );
        suspicionResponse = new SuspicionOperationDto(false);
    }

    @Test
    void checkTransferOperation_ShouldCallCorrectEndpoint() {
        when(restTemplate.postForObject(anyString(), any(), any()))
            .thenReturn(suspicionResponse);

        blockerClient.checkTransferOperation(transferRequest);

        verify(restTemplate).postForObject(
            "http://blocker-service/fraud-check/transfer",
            transferRequest,
            SuspicionOperationDto.class
        );
    }

    @Test
    void checkTransferOperation_ShouldReturnResponse() {
        when(restTemplate.postForObject(anyString(), any(), any()))
            .thenReturn(suspicionResponse);

        SuspicionOperationDto result = blockerClient.checkTransferOperation(transferRequest);

        assertNotNull(result);
        assertFalse(result.isSuspicious());
    }
}