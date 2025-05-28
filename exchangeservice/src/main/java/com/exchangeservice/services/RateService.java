package com.exchangeservice.services;

import com.exchangeservice.dto.CurrencyRate;
import com.exchangeservice.dto.ExchangeRate;

import java.util.List;

public interface RateService {

    void saveRates(List<CurrencyRate> newRates);

    List<ExchangeRate> getLatestRates();

    ExchangeRate getLatestRateByName(String name);
}
