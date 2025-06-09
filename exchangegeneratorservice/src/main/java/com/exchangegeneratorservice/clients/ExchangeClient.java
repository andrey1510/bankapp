package com.exchangegeneratorservice.clients;

import com.exchangegeneratorservice.dto.CurrencyRate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ExchangeClient {

    private static final Random RANDOM = new Random();
    private static final int SCALE = 2;

    private final RestTemplate restTemplate;

    @Value("${exchangeservice.url}")
    protected String exchangeServiceUrl;

    @Scheduled(fixedRate = 1000)
    public void scheduledGenerateAndSendRates() {
        generateAndSendRates();
    }

    public ResponseEntity<Void> generateAndSendRates() {
        try {
            List<CurrencyRate> rates = List.of(
                new CurrencyRate("Рубль", "RUR", BigDecimal.ONE, LocalDateTime.now()),
                new CurrencyRate("Доллар", "USD", generateRate(70, 20), LocalDateTime.now()),
                new CurrencyRate("Юань", "CNY", generateRate(10, 5), LocalDateTime.now())
            );

            restTemplate.postForObject(exchangeServiceUrl, rates, Void.class);

            return ResponseEntity.accepted().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    protected BigDecimal generateRate(double base, double spread) {

        double randomFactor = spread * RANDOM.nextDouble();

        return new BigDecimal(Double.toString(base + randomFactor))
            .setScale(SCALE, RoundingMode.HALF_UP);
    }

}