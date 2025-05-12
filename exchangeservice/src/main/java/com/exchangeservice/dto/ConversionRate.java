package com.exchangeservice.dto;

public record ConversionRate(
    String currency,
    Double sellRate,
    Double buyRate
) {}
