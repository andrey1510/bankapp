package com.frontservice.clients;

import com.frontservice.dto.CurrenciesDto;
import com.frontservice.dto.RatesDto;
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
public class ExchangeClient {

    @Value("${exchangeservice.url}")
    protected String exchangeUrl;

    @Qualifier("exchangeRestTemplate")
    private final RestTemplate restTemplate;

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 2, backoff = @Backoff(delay = 1000)
    )
    public CurrenciesDto getCurrenciesDto() {
        return restTemplate.getForEntity(
            String.format("%s/currencies", exchangeUrl),
            CurrenciesDto.class
        ).getBody();
    }

    public RatesDto getRates() {
        return restTemplate.getForEntity(
            String.format("%s/rates", exchangeUrl),
            RatesDto.class
        ).getBody();
    }
}
