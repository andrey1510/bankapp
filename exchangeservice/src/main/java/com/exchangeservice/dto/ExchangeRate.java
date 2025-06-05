package com.exchangeservice.dto;

public record ExchangeRate(
    String title,
    String currency,
    Double value
) {}
