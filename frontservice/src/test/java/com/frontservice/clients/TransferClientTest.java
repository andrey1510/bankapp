package com.frontservice.clients;

import com.frontservice.dto.TransferRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransferClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TransferClient transferClient;

    private final String testUrl = "http://transfer-service";
    private final TransferRequestDto testRequest = new TransferRequestDto(
        "user@example.com",
        12345L,
        "USD",
        new BigDecimal("100.00"),
        67890L,
        "EUR",
        "login1",
        "login2"
    );

    @BeforeEach
    void setUp() {
        transferClient.transferserviceUrl = testUrl;
    }

    @Test
    void sendTransferRequest_ShouldCallTransferService() {
        transferClient.sendTransferRequest(testRequest);

        verify(restTemplate).postForEntity(
            eq(testUrl + "/transfers/transfer"),
            eq(testRequest),
            eq(Void.class)
        );
    }

    @Test
    void sendTransferRequest_WithNegativeAmount_ShouldCallTransferService() {
        TransferRequestDto negativeAmountRequest = new TransferRequestDto(
            "user@example.com",
            12345L,
            "USD",
            new BigDecimal("-50.00"),
            67890L,
            "EUR",
            "login1",
            "login2"
        );

        transferClient.sendTransferRequest(negativeAmountRequest);

        verify(restTemplate).postForEntity(
            eq(testUrl + "/transfers/transfer"),
            eq(negativeAmountRequest),
            eq(Void.class)
        );
    }

}
