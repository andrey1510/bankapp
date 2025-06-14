package com.transferservice.clients;

import com.transferservice.dto.BalanceUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

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
    public void updateBalances(BalanceUpdateRequestDto updateRequest) {
        restTemplate.postForObject(
            String.format("%s/accounts/transfer-update", accountServiceUrl),
            updateRequest,
            Void.class
        );
    }
}
