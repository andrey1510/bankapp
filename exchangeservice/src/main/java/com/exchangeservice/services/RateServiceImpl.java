package com.exchangeservice.services;

import com.exchangeservice.dto.ConversionRateDto;
import com.exchangeservice.dto.ConversionRateRequestDto;
import com.exchangeservice.dto.CurrencyRate;
import com.exchangeservice.dto.ExchangeRate;
import com.exchangeservice.dto.CurrenciesDto;
import com.exchangeservice.entities.Rate;
import com.exchangeservice.repositories.RateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RateServiceImpl implements RateService {

    private final RateRepository rateRepository;

    @Override
    @Transactional
    public void saveRates(List<CurrencyRate> currencyRates) {
        rateRepository.saveAll(currencyRates.stream()
            .map(rate -> Rate.builder()
                .title(rate.title())
                .currency(rate.currency())
                .value(rate.value())
                .timestamp(rate.timestamp())
                .build()
            )
            .collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ExchangeRate> getLatestRates() {
        return rateRepository.findLatestRates().stream()
            .map(rate -> new ExchangeRate(
                rate.getTitle(),
                rate.getCurrency(),
                rate.getValue()
            ))
            .collect(Collectors.toList());
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

        if (dto.fromCurrency().equals(dto.toCurrency())) return new ConversionRateDto(1.0);

        ExchangeRate fromRate = getLatestRateByCurrency(dto.fromCurrency());
        ExchangeRate toRate = getLatestRateByCurrency(dto.toCurrency());

        if ("RUR".equals(dto.fromCurrency())) {
            return new ConversionRateDto(1 / toRate.value());
        } else if ("RUR".equals(dto.toCurrency())) {
            return new ConversionRateDto(fromRate.value());
        }

        double rubles = fromRate.value();
        double targetValue = 1 / toRate.value();

        return new ConversionRateDto(rubles * targetValue);

    }


}
