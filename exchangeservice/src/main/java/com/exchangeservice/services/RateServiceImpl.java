package com.exchangeservice.services;

import com.exchangeservice.dto.CurrencyRate;
import com.exchangeservice.dto.ExchangeRate;
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
                .name(rate.name())
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
                rate.getName(),
                rate.getValue()
            ))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ExchangeRate getLatestRateByName(String name) {
        return rateRepository.findLatestRateByName(name)
            .map(rate -> new ExchangeRate(
                rate.getTitle(),
                rate.getName(),
                rate.getValue()
            )).orElseThrow();

    }
}
