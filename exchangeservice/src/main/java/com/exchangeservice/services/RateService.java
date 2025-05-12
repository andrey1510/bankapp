package com.exchangeservice.services;

import com.exchangeservice.dto.ConversionRate;
import com.exchangeservice.dto.CurrencyRate;

import java.util.List;

public interface RateService {

    void updateRates(List<CurrencyRate> newRates);

    List<ConversionRate> getAllRates();

    ConversionRate getRateByCurrency(String currency);
}
