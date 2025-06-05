package com.exchangeservice.services;

import com.exchangeservice.dto.ConversionRateDto;
import com.exchangeservice.dto.ConversionRateRequestDto;
import com.exchangeservice.dto.CurrencyRate;
import com.exchangeservice.dto.ExchangeRate;
import com.exchangeservice.dto.CurrenciesDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RateService {

    void saveRates(List<CurrencyRate> newRates);

    List<ExchangeRate> getLatestRates();

    ExchangeRate getLatestRateByCurrency(String name);

    @Transactional(readOnly = true)
    CurrenciesDto getCurrencies();

    @Transactional(readOnly = true)
    ConversionRateDto getConversionRate(ConversionRateRequestDto dto);
}
