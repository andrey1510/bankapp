package com.transferservice.dto;

public record ExchangeRateResponse(
    String currency,
    Double sellRate,
    Double buyRate
) {}
