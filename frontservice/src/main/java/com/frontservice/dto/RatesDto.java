package com.frontservice.dto;

import java.util.List;

public record RatesDto(
    List<ExchangeRate> rates
) {}
