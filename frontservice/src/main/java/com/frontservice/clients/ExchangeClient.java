package com.frontservice.clients;

import com.frontservice.dto.CurrenciesDto;
import com.frontservice.dto.RatesDto;
import com.frontservice.exceptions.RatesFetchException;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeClient {

    @Value("${exchangeservice.url}")
    protected String exchangeUrl;

    @Qualifier("exchangeRestTemplate")
    private final RestTemplate restTemplate;
    private final MeterRegistry meterRegistry;

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 2, backoff = @Backoff(delay = 1000)
    )
    public CurrenciesDto getCurrenciesDto() {
        CurrenciesDto currencies = restTemplate.getForEntity(
            String.format("%s/currencies", exchangeUrl),
            CurrenciesDto.class
        ).getBody();
        log.info("Currencies: {}", currencies);
        return currencies;
    }

    public RatesDto getRates() throws RatesFetchException {
        try {
            RatesDto rates = restTemplate.getForEntity(
                String.format("%s/rates", exchangeUrl),
                RatesDto.class
            ).getBody();
            log.info("Rates: {}", rates);

            return rates;
        } catch (ResourceAccessException | HttpClientErrorException | HttpServerErrorException ex) {
            meterRegistry.counter("rates_update_failed").increment();
            log.error("Rates update failed", ex);
            throw new RatesFetchException("Rates update failed");
        }
    }
}
