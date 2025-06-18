package com.exchangeservice.services;

import com.exchangeservice.dto.ConversionRateDto;
import com.exchangeservice.dto.ConversionRateRequestDto;
import com.exchangeservice.dto.CurrenciesDto;
import com.exchangeservice.dto.CurrencyRateDto;
import com.exchangeservice.dto.ExchangeRate;
import com.exchangeservice.entities.Rate;
import com.exchangeservice.repositories.RateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RateServiceImplTest {

    @Mock
    private RateRepository rateRepository;

    @InjectMocks
    private RateServiceImpl rateService;

    private List<Rate> testRates;
    private List<CurrencyRateDto> testCurrencyRateDtos;
    private LocalDateTime testTimestamp;

    @BeforeEach
    void setUp() {
        testTimestamp = LocalDateTime.now();

        testRates = List.of(
            Rate.builder()
                .title("Доллар")
                .currency("USD")
                .value(new BigDecimal("75.00"))
                .timestamp(testTimestamp)
                .build(),
            Rate.builder()
                .title("Евро").currency("EUR")
                .value(new BigDecimal("850.00"))
                .timestamp(testTimestamp)
                .build(),
            Rate.builder()
                .title("Рубль")
                .currency("RUR")
                .value(new BigDecimal("1.00"))
                .timestamp(testTimestamp)
                .build()
        );

        testCurrencyRateDtos = List.of(
            new CurrencyRateDto("Доллар", "USD", new BigDecimal("75.00"), testTimestamp),
            new CurrencyRateDto("Евро", "EUR", new BigDecimal("85.00"), testTimestamp)
        );
    }

    @Test
    void saveRates_ShouldSaveAllRates() {

        rateService.saveRates(testCurrencyRateDtos);

        verify(rateRepository).saveAll(argThat((List<Rate> list) ->
            list.size() == 2 &&
                list.stream().anyMatch(r -> r.getCurrency().equals("USD")) &&
                list.stream().anyMatch(r -> r.getCurrency().equals("EUR"))
        ));
        verify(rateRepository, times(1)).saveAll(anyList());
    }

    @Test
    void getLatestRates_ShouldReturnConvertedRates() {

        when(rateRepository.findLatestRates()).thenReturn(testRates);

        List<ExchangeRate> result = rateService.getLatestRates().rates();

        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(r -> r.currency().equals("USD")));
        verify(rateRepository, times(1)).findLatestRates();
    }

    @Test
    void getLatestRateByCurrency_ShouldReturnCorrectRate() {

        Rate usdRate = testRates.getFirst();
        when(rateRepository.findLatestRateByCurrency("USD")).thenReturn(Optional.of(usdRate));

        ExchangeRate result = rateService.getLatestRateByCurrency("USD");

        assertEquals("USD", result.currency());
        assertEquals(new BigDecimal("75.00"), result.value());
        verify(rateRepository, times(1)).findLatestRateByCurrency("USD");
    }

    @Test
    void getLatestRateByCurrency_ShouldThrowWhenNotFound() {

        when(rateRepository.findLatestRateByCurrency("XXX")).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> rateService.getLatestRateByCurrency("XXX"));
    }

    @Test
    void getCurrencies_ShouldReturnAllCurrencies() {

        Map<String, String> currencyMap = new HashMap<>();
        currencyMap.put("USD", "Доллар");
        currencyMap.put("EUR", "Евро");

        when(rateRepository.findAllCurrencyNamesWithTitles()).thenReturn(currencyMap);

        CurrenciesDto result = rateService.getCurrencies();

        assertNotNull(result.currencies());
        assertEquals(2, result.currencies().size());
        assertEquals("Доллар", result.currencies().get("USD"));
        assertEquals("Евро", result.currencies().get("EUR"));
    }

    @Test
    void getConversionRate_ShouldReturnOneForSameCurrency() {

        ConversionRateDto result = rateService.getConversionRate(
            new ConversionRateRequestDto("USD", "USD")
        );

        assertEquals(new BigDecimal("1.00"), result.rate());
        verifyNoInteractions(rateRepository);
    }

}
