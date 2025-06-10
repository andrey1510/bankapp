package com.transferservice.dto;

public record ConversionRateRequestDto(
    String fromCurrency,
    String toCurrency
) {}
