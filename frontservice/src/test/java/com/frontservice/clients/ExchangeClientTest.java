package com.frontservice.clients;

import com.frontservice.dto.CurrenciesDto;
import com.frontservice.dto.ExchangeRate;
import com.frontservice.dto.RatesDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class ExchangeClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ExchangeClient exchangeClient;

    private final String currenciesUrl = "http://exchange-service/currencies";
    private final String ratesUrl = "http://exchange-service/rates";

    @BeforeEach
    void setUp() {
        exchangeClient.currenciesUrl = currenciesUrl;
        exchangeClient.ratesUrl = ratesUrl;
    }

    @Test
    void getCurrenciesDto_ShouldReturnCurrencies() {
        Map<String, String> expectedCurrencies = Map.of(
            "USD", "Доллар США",
            "EUR", "Евро"
        );
        CurrenciesDto expectedResponse = new CurrenciesDto(expectedCurrencies);

        when(restTemplate.getForEntity(
            eq(currenciesUrl),
            eq(CurrenciesDto.class)
        )).thenReturn(ResponseEntity.ok(expectedResponse));

        CurrenciesDto result = exchangeClient.getCurrenciesDto();

        assertEquals(expectedResponse, result);
    }

    @Test
    void getRates_ShouldReturnRates() {
        List<ExchangeRate> expectedRates = List.of(
            new ExchangeRate("Доллар США", "USD", new BigDecimal("75.50")),
            new ExchangeRate("Евро", "EUR", new BigDecimal("85.30"))
        );
        RatesDto expectedResponse = new RatesDto(expectedRates);

        when(restTemplate.getForEntity(
            eq(ratesUrl),
            eq(RatesDto.class)
        )).thenReturn(ResponseEntity.ok(expectedResponse));

        RatesDto result = exchangeClient.getRates();

        assertEquals(expectedResponse, result);
    }

    @Test
    void getRates_ShouldReturnEmptyRates() {
        RatesDto expectedResponse = new RatesDto(List.of());

        when(restTemplate.getForEntity(
            eq(ratesUrl),
            eq(RatesDto.class)
        )).thenReturn(ResponseEntity.ok(expectedResponse));

        RatesDto result = exchangeClient.getRates();

        assertEquals(expectedResponse, result);
        assertEquals(0, result.rates().size());
    }
}
