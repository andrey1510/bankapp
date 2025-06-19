package com.exchangegeneratorservice.services;

import com.exchangegeneratorservice.dto.kafka.CurrencyRateDto;
import com.exchangegeneratorservice.dto.kafka.CurrencyRatesBatchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeGenerationService {

    private static final Random RANDOM = new Random();
    private static final int SCALE = 2;

    public CurrencyRatesBatchDto getCurrencyRateDtos() {
        return new CurrencyRatesBatchDto(List.of(
            new CurrencyRateDto("Рубль", "RUR", BigDecimal.ONE, LocalDateTime.now()),
            new CurrencyRateDto("Доллар", "USD", generateRate(70, 20), LocalDateTime.now()),
            new CurrencyRateDto("Юань", "CNY", generateRate(10, 5), LocalDateTime.now())
        ));
    }

    protected BigDecimal generateRate(double base, double spread) {
        double randomFactor = spread * RANDOM.nextDouble();
        return new BigDecimal(Double.toString(base + randomFactor))
            .setScale(SCALE, RoundingMode.HALF_UP);
    }
}