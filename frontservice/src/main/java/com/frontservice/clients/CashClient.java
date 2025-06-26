package com.frontservice.clients;

import com.frontservice.dto.CashRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashClient {

    @Value("${cashservice.url}")
    protected String cashserviceUrl;

    @Qualifier("cashRestTemplate")
    private final RestTemplate restTemplate;

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 2, backoff = @Backoff(delay = 1000)
    )
    public void sendCashRequest(CashRequestDto request) {
        restTemplate.postForEntity(
            String.format("%s/cash/operation", cashserviceUrl),
            request,
            Void.class
        );
        log.info("Send cash request");
    }

}
