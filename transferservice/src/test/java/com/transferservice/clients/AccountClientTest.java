package com.transferservice.clients;

import com.transferservice.dto.BalanceUpdateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AccountClient accountClient;

    private BalanceUpdateRequestDto balanceUpdateRequest;

    @BeforeEach
    void setUp() {
        accountClient.accountServiceUrl = "http://account-service";
        balanceUpdateRequest = new BalanceUpdateRequestDto(
            1L,
            new BigDecimal("100.00"),
            2L,
            new BigDecimal("85.00")
        );
    }

    @Test
    void updateBalances_ShouldCallRestTemplateWithCorrectUrl() {
        accountClient.updateBalances(balanceUpdateRequest);
        verify(restTemplate).postForObject(
            "http://account-service/accounts/transfer-update",
            balanceUpdateRequest,
            Void.class
        );
    }

    @Test
    void updateBalances_ShouldPassCorrectRequestData() {
        accountClient.updateBalances(balanceUpdateRequest);

        verify(restTemplate).postForObject(
            anyString(),
            eq(balanceUpdateRequest),
            eq(Void.class)
        );
    }
}
