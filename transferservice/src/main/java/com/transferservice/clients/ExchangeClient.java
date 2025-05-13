package com.transferservice.clients;

import com.transferservice.dto.ExchangeRateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ExchangeClient {

    private final RestTemplate restTemplate;

    @Value("${exchangeservice.url}")
    private String exchangeServiceUrl;

    public Double getConversionRate(String fromCurrency, String toCurrency) {

        if (fromCurrency.equalsIgnoreCase(toCurrency)) return 1.0;

        ExchangeRateResponse fromRate = restTemplate.getForObject(
            exchangeServiceUrl + "/" + fromCurrency,
            ExchangeRateResponse.class
        );

        ExchangeRateResponse toRate = restTemplate.getForObject(
            exchangeServiceUrl + "/" + toCurrency,
            ExchangeRateResponse.class
        );

        if (fromRate == null || toRate == null) throw new RuntimeException("Курс не найден");

        return (fromRate.sellRate() / toRate.buyRate());
    }
}
