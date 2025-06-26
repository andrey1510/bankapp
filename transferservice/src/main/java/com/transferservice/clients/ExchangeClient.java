package com.transferservice.clients;

import com.transferservice.dto.ConversionRateDto;
import com.transferservice.dto.ConversionRateRequestDto;
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
public class ExchangeClient {

    @Value("${exchangeservice.url}")
    protected String exchangeServiceUrl;

    @Qualifier("exchangeRestTemplate")
    private final RestTemplate restTemplate;

    @Retryable(retryFor = {ResourceAccessException.class, SocketTimeoutException.class, ConnectException.class},
        maxAttempts = 2, backoff = @Backoff(delay = 1000)
    )
    public ConversionRateDto getConversionRate(ConversionRateRequestDto requestDto) {
        ConversionRateDto conversionRateDto = restTemplate.postForObject(
            String.format("%s/rates/conversion", exchangeServiceUrl),
            requestDto,
            ConversionRateDto.class
        );
        log.info("Conversion rate sent: {}", conversionRateDto);
        return conversionRateDto;

    }
}
