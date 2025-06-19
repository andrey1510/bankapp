package com.exchangeservice.services;

import com.exchangeservice.dto.ConversionRateDto;
import com.exchangeservice.dto.ConversionRateRequestDto;
import com.exchangeservice.dto.kafka.CurrencyRatesBatchDto;
import com.exchangeservice.dto.ExchangeRate;
import com.exchangeservice.dto.CurrenciesDto;
import com.exchangeservice.dto.RatesDto;

public interface RateService {

    void saveRates(CurrencyRatesBatchDto batch);

    RatesDto getLatestRates();

    ExchangeRate getLatestRateByCurrency(String name);

    CurrenciesDto getCurrencies();

    ConversionRateDto getConversionRate(ConversionRateRequestDto dto);
}
