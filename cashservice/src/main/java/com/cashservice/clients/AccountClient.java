package com.cashservice.clients;

import com.cashservice.dto.AccountBalanceChangeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountClient {

    @Value("${accountservice.url}")
    protected String accountServiceUrl;

    @Qualifier("accountRestTemplate")
    private final RestTemplate restTemplate;

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 2, backoff = @Backoff(delay = 1000)
    )
    public void sendAccountRequest(Long accountId, BigDecimal amount, String login) {
        restTemplate.postForObject(
            String.format("%s/accounts/cash-update", accountServiceUrl),
            new AccountBalanceChangeDto(accountId, amount, login),
            Void.class
        );
        log.info("Send account request");
    }

}
