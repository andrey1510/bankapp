package com.exchangeservice.controllers;

import com.exchangeservice.dto.ConversionRateDto;
import com.exchangeservice.dto.ConversionRateRequestDto;
import com.exchangeservice.dto.ExchangeRate;
import com.exchangeservice.dto.CurrenciesDto;
import com.exchangeservice.dto.RatesDto;
import com.exchangeservice.services.RateService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RateController {

    private final RateService rateService;

    @GetMapping("/rates")
    @PreAuthorize("hasAuthority('SCOPE_exchangeservice.get')")
    public RatesDto getLatestRates() {
        return rateService.getLatestRates();
    }

    @GetMapping("/rates/{name}")
    @PreAuthorize("hasAuthority('SCOPE_exchangeservice.get')")
    public ExchangeRate getRateByName(@PathVariable String name) {
        return rateService.getLatestRateByCurrency(name);
    }

    @PostMapping("/rates/conversion")
    @PreAuthorize("hasAuthority('SCOPE_exchangeservice.post')")
    public ConversionRateDto getConversionRate(@RequestBody ConversionRateRequestDto dto) {
        return rateService.getConversionRate(dto);
    }

    @GetMapping("/currencies")
    @PreAuthorize("hasAuthority('SCOPE_exchangeservice.get')")
    public CurrenciesDto getCurrencies() {
        return rateService.getCurrencies();
    }

}
