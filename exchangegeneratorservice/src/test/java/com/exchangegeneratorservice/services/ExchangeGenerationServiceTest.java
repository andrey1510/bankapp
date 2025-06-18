package com.exchangegeneratorservice.services;
import com.exchangegeneratorservice.dto.CurrencyRateDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ExchangeGenerationServiceTest {

    @InjectMocks
    private ExchangeGenerationService exchangeGenerationService;

    @Test
    void getCurrencyRateDtos_GeneratesValidRates() {
        List<CurrencyRateDto> rates = exchangeGenerationService.getCurrencyRateDtos();

        assertEquals(3, rates.size());
        assertTrue(rates.stream().anyMatch(r -> r.currency().equals("RUR")));
        assertTrue(rates.stream().anyMatch(r -> r.currency().equals("USD")));
        assertTrue(rates.stream().anyMatch(r -> r.currency().equals("CNY")));

        CurrencyRateDto usdRate = rates.stream()
            .filter(r -> r.currency().equals("USD"))
            .findFirst()
            .orElseThrow();
        assertTrue(usdRate.value().compareTo(BigDecimal.valueOf(50)) >= 0);
        assertTrue(usdRate.value().compareTo(BigDecimal.valueOf(90)) <= 0);
    }
}