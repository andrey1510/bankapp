package com.exchangeservice.services;

import com.exchangeservice.dto.ConversionRateDto;
import com.exchangeservice.dto.ConversionRateRequestDto;
import com.exchangeservice.dto.CurrencyRate;
import com.exchangeservice.dto.ExchangeRate;
import com.exchangeservice.dto.CurrenciesDto;
import com.exchangeservice.dto.RatesDto;

import java.util.List;

public interface RateService {

    void saveRates(List<CurrencyRate> newRates);

    RatesDto getLatestRates();

    ExchangeRate getLatestRateByCurrency(String name);

    CurrenciesDto getCurrencies();

    ConversionRateDto getConversionRate(ConversionRateRequestDto dto);
}
