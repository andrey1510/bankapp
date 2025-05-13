package com.exchangegeneratorservice.clients;

import com.exchangegeneratorservice.dto.CurrencyRate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ExchangeClient {

    private static final Random RANDOM = new Random();

    private final RestTemplate restTemplate;

    @Value("${exchangeservice.url}")
    private String exchangeServiceUrl;

    @Scheduled(fixedRate = 1000)
    public void generateAndSendRates() {

        List<CurrencyRate> rates = List.of(
            new CurrencyRate("usd", generateRate(70, 20)),
            new CurrencyRate("cny", generateRate(10, 5))
        );

        restTemplate.postForObject(exchangeServiceUrl, rates, Void.class);
    }

    private double generateRate(double base, double spread) {

        double value = base + spread * RANDOM.nextDouble();

        return Math.round(value * 100.0) / 100.0;
    }

}