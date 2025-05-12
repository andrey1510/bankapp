package com.exchangeservice.services;

import com.exchangeservice.dto.ConversionRate;
import com.exchangeservice.dto.CurrencyRate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateServiceImpl implements RateService {

    private final Map<String, ConversionRate> rates = new ConcurrentHashMap<>();

    @Override
    public void updateRates(List<CurrencyRate> newRates) {
        newRates.forEach(rate -> {
            ConversionRate conversionRate = convertRate(rate);
            rates.put(conversionRate.currency(), conversionRate);
        });
    }

    public List<ConversionRate> getAllRates() {
        return new ArrayList<>(rates.values());
    }

    public ConversionRate getRateByCurrency(String currency) {
        return rates.get(currency.toUpperCase());
    }

    private ConversionRate convertRate(CurrencyRate currencyRate) {
        double baseRate = currencyRate.rate();
        return new ConversionRate(
            currencyRate.ticker(),
            Math.round(baseRate * 1.01 * 100.0) / 100.0,
            Math.round(baseRate * 0.99 * 100.0) / 100.0
        );
    }
}
