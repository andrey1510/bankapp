package com.exchangeservice.controllers;

import com.exchangeservice.dto.ConversionRateDto;
import com.exchangeservice.dto.ConversionRateRequestDto;
import com.exchangeservice.dto.CurrenciesDto;
import com.exchangeservice.dto.kafka.CurrencyRateDto;
import com.exchangeservice.dto.ExchangeRate;
import com.exchangeservice.dto.RatesDto;
import com.exchangeservice.services.RateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateControllerTest {

    @Mock
    private RateService rateService;

    @InjectMocks
    private RateController rateController;

    private List<CurrencyRateDto> rates;
    private ConversionRateRequestDto conversionRequest;
    private RatesDto ratesDto;
    private ExchangeRate exchangeRate;
    private ConversionRateDto conversionRateDto;
    private CurrenciesDto currenciesDto;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        rates = Arrays.asList(
            new CurrencyRateDto("USD Rate", "USD", new BigDecimal("1.0"), now),
            new CurrencyRateDto("EUR Rate", "EUR", new BigDecimal("0.85"), now)
        );

        conversionRequest = new ConversionRateRequestDto(
            "USD",
            "EUR"
        );

        ratesDto = new RatesDto(
            Arrays.asList(
                new ExchangeRate("Доллары", "USD", new BigDecimal("1.0")),
                new ExchangeRate("Евро", "EUR", new BigDecimal("0.85"))
            )
        );

        exchangeRate = new ExchangeRate("Доллары","USD", new BigDecimal("1.0"));

        conversionRateDto = new ConversionRateDto(new BigDecimal("0.85"));

        Map<String, String> currenciesMap = new HashMap<>();
        currenciesMap.put("USD", "US Dollar");
        currenciesMap.put("EUR", "Euro");
        currenciesMap.put("GBP", "British Pound");
        currenciesDto = new CurrenciesDto(currenciesMap);
    }

    @Test
    void getLatestRates_ShouldReturnRates() {
        when(rateService.getLatestRates()).thenReturn(ratesDto);

        RatesDto result = rateController.getLatestRates();

        assertEquals(ratesDto, result);
        verify(rateService, times(1)).getLatestRates();
    }

    @Test
    void getRateByName_ShouldReturnRate() {
        when(rateService.getLatestRateByCurrency("USD")).thenReturn(exchangeRate);

        ExchangeRate result = rateController.getRateByName("USD");

        assertEquals(exchangeRate, result);
        verify(rateService, times(1)).getLatestRateByCurrency("USD");
    }

    @Test
    void getConversionRate_ShouldReturnConversionRate() {
        when(rateService.getConversionRate(any())).thenReturn(conversionRateDto);

        ConversionRateDto result = rateController.getConversionRate(conversionRequest);

        assertEquals(conversionRateDto, result);
        verify(rateService, times(1)).getConversionRate(conversionRequest);
    }

    @Test
    void getCurrencies_ShouldReturnCurrencies() {
        when(rateService.getCurrencies()).thenReturn(currenciesDto);

        CurrenciesDto result = rateController.getCurrencies();

        assertEquals(currenciesDto, result);
        verify(rateService, times(1)).getCurrencies();
    }
} 