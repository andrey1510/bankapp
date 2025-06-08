package com.frontservice.dto;

public record ExchangeRate(
    String title,
    String currency,
    Double value
) {}
