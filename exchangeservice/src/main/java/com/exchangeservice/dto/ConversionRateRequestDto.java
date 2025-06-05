package com.exchangeservice.dto;

public record ConversionRateRequestDto(
    String fromCurrency,
    String toCurrency
) {}
