package com.exchangeservice.controllers;

import com.exchangeservice.dto.ConversionRate;
import com.exchangeservice.dto.CurrencyRate;
import com.exchangeservice.services.RateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/rates")
@RequiredArgsConstructor
public class RateController {

    private final RateService rateStorage;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void receiveRates(@RequestBody List<CurrencyRate> rates) {
        rateStorage.updateRates(rates);
    }

    @GetMapping
    public List<ConversionRate> getAllRates() {
        return rateStorage.getAllRates();
    }

    @GetMapping("/{currency}")
    public ConversionRate getRateByCurrency(@PathVariable String currency) {

        ConversionRate rate = rateStorage.getRateByCurrency(currency);

        if (rate == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return rate;
    }
}
