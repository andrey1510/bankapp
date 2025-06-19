package com.exchangeservice.services;

import com.exchangeservice.dto.ConversionRateDto;
import com.exchangeservice.dto.ConversionRateRequestDto;
import com.exchangeservice.dto.kafka.CurrencyRatesBatchDto;
import com.exchangeservice.dto.ExchangeRate;
import com.exchangeservice.dto.CurrenciesDto;
import com.exchangeservice.dto.RatesDto;
import com.exchangeservice.entities.Rate;
import com.exchangeservice.repositories.RateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateServiceImpl implements RateService {

    private final RateRepository rateRepository;

    @Transactional
    @Override
    public void saveRates(CurrencyRatesBatchDto batch) {
        List<Rate> rates = batch.getRates().stream()
            .map(rate -> Rate.builder()
                .title(rate.getTitle())
                .currency(rate.getCurrency())
                .value(rate.getValue())
                .timestamp(rate.getTimestamp())
                .build()
            )
            .collect(Collectors.toList());
        log.info("Saving rates: {}", rates);
        rateRepository.saveAll(rates);
    }

    @Transactional(readOnly = true)
    @Override
    public RatesDto getLatestRates() {
        return new RatesDto(
            rateRepository.findLatestRates().stream()
                .map(rate -> new ExchangeRate(
                    rate.getTitle(),
                    rate.getCurrency(),
                    rate.getValue()
                ))
                .toList()
        );
    }

    @Transactional(readOnly = true)
    @Override
    public ExchangeRate getLatestRateByCurrency(String currency) {
        return rateRepository.findLatestRateByCurrency(currency)
            .map(rate -> new ExchangeRate(
                rate.getTitle(),
                rate.getCurrency(),
                rate.getValue()
            )).orElseThrow();

    }

    @Transactional(readOnly = true)
    @Override
    public CurrenciesDto getCurrencies() {
        return new CurrenciesDto(rateRepository.findAllCurrencyNamesWithTitles());
    }


    @Transactional(readOnly = true)
    @Override
    public ConversionRateDto getConversionRate(ConversionRateRequestDto dto) {

        if (dto.fromCurrency().equals(dto.toCurrency())) return new ConversionRateDto(new BigDecimal("1.00"));

        ExchangeRate fromRate = getLatestRateByCurrency(dto.fromCurrency());
        ExchangeRate toRate = getLatestRateByCurrency(dto.toCurrency());

        if ("RUR".equals(dto.fromCurrency())) {
            BigDecimal rate = BigDecimal.ONE.divide(toRate.value(), 2, RoundingMode.HALF_UP);
            return new ConversionRateDto(rate);
        } else if ("RUR".equals(dto.toCurrency())) {
            return new ConversionRateDto(fromRate.value());
        }

        BigDecimal inverseToRate = BigDecimal.ONE.divide(toRate.value(), 2, RoundingMode.HALF_UP);
        return new ConversionRateDto(fromRate.value().multiply(inverseToRate).setScale(2, RoundingMode.HALF_UP));
    }

}
