package com.exchangeservice.controllers;

import com.exchangeservice.dto.CurrencyRate;
import com.exchangeservice.dto.ExchangeRate;
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

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RateController {

    private final RateService rateService;

    @PostMapping("/generation")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void receiveRates(@RequestBody List<CurrencyRate> rates) {
        rateService.saveRates(rates);
    }

    @GetMapping("/rates")
    public List<ExchangeRate> getLatestRates() {
        return rateService.getLatestRates();
    }

    @GetMapping("/rates/{name}")
    public ExchangeRate getRateByName(@PathVariable String name) {
        return rateService.getLatestRateByName(name.toUpperCase());
    }
}
