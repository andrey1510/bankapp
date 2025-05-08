package com.exchangegeneratorservice.services;

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
public class ExchangeGeneratorService {

    private static final Random RANDOM = new Random();

    private final RestTemplate restTemplate;

    @Value("${exchangeservice.url}")
    private String exchangeServiceUrl;

    @Scheduled(fixedRate = 1000)
    public void generateAndSendRates() {

        List<CurrencyRate> rates = List.of(
            new CurrencyRate("USD", String.format("%.2f", 70 + 20 * RANDOM.nextDouble())),
            new CurrencyRate("CNY", String.format("%.2f", 10 + 5 * RANDOM.nextDouble()))
        );

        try {
            restTemplate.postForObject(exchangeServiceUrl, rates, Void.class);
        } catch (Exception e) {
            System.err.println("Error sending rates: " + e.getMessage());
        }
    }

}
