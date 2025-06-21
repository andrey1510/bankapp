package com.frontservice.clients;

import com.frontservice.dto.CashRequestDto;
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
class CashClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CashClient cashClient;

    private final String cashServiceUrl = "http://cash-service";
    private final CashRequestDto testRequest = new CashRequestDto(
        "user@example.com",
        12345L,
        "USD",
        new BigDecimal("100.00"),
        true,
        "login"
    );

    @BeforeEach
    void setUp() {
        cashClient.cashserviceUrl = cashServiceUrl;
    }

    @Test
    void sendCashRequest_ShouldCallCashServiceForDeposit() {
        CashRequestDto depositRequest = new CashRequestDto(
            "user@example.com",
            12345L,
            "USD",
            new BigDecimal("100.00"),
            true,
            "login"
        );

        cashClient.sendCashRequest(depositRequest);

        verify(restTemplate).postForEntity(
            eq(cashServiceUrl + "/cash/operation"),
            eq(depositRequest),
            eq(Void.class)
        );
    }

    @Test
    void sendCashRequest_ShouldCallCashServiceForWithdrawal() {
        CashRequestDto withdrawalRequest = new CashRequestDto(
            "user@example.com",
            12345L,
            "USD",
            new BigDecimal("50.00"),
            false,
            "login"
        );

        cashClient.sendCashRequest(withdrawalRequest);

        verify(restTemplate).postForEntity(
            eq(cashServiceUrl + "/cash/operation"),
            eq(withdrawalRequest),
            eq(Void.class)
        );
    }

    @Test
    void sendCashRequest_ShouldHandleNegativeAmount() {
        CashRequestDto negativeAmountRequest = new CashRequestDto(
            "user@example.com",
            12345L,
            "USD",
            new BigDecimal("-100.00"),
            true,
            "login"
        );

        cashClient.sendCashRequest(negativeAmountRequest);

        verify(restTemplate).postForEntity(
            eq(cashServiceUrl + "/cash/operation"),
            eq(negativeAmountRequest),
            eq(Void.class)
        );
    }

    @Test
    void sendCashRequest_ShouldHandleZeroAmount() {
        CashRequestDto zeroAmountRequest = new CashRequestDto(
            "user@example.com",
            12345L,
            "USD",
            BigDecimal.ZERO,
            true,
            "login"
        );

        cashClient.sendCashRequest(zeroAmountRequest);

        verify(restTemplate).postForEntity(
            eq(cashServiceUrl + "/cash/operation"),
            eq(zeroAmountRequest),
            eq(Void.class)
        );
    }
}
