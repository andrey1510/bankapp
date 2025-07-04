package com.cashservice.clients;

import com.cashservice.dto.AccountBalanceChangeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AccountClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AccountClient accountClient;

    private Long testAccountId;
    private BigDecimal testAmount;
    private AccountBalanceChangeDto expectedDto;

    @BeforeEach
    void setUp() {
        testAccountId = 123L;
        testAmount = new BigDecimal("100.00");
        expectedDto = new AccountBalanceChangeDto(testAccountId, testAmount, "login");
        accountClient.accountServiceUrl = "http://localhost:8080";
    }

    @Test
    void sendAccountRequest_shouldCallRestTemplateWithCorrectUrl() {
        accountClient.sendAccountRequest(testAccountId, testAmount, "login");
        verify(restTemplate).postForObject(
            eq("http://localhost:8080/accounts/cash-update"),
            eq(expectedDto),
            eq(Void.class)
        );
    }

    @Test
    void sendAccountRequest_shouldPassCorrectDto() {
        accountClient.sendAccountRequest(testAccountId, testAmount, "login");
        verify(restTemplate).postForObject(
            anyString(),
            eq(expectedDto),
            any()
        );
    }

    @Test
    void sendAccountRequest_shouldUseVoidReturnType() {
        accountClient.sendAccountRequest(testAccountId, testAmount, "login");
        verify(restTemplate).postForObject(
            anyString(),
            any(),
            eq(Void.class)
        );
    }
}