package com.exchangeservice.dto;

import java.util.Map;

public record CurrenciesDto(
    Map<String, String> currencies
) {
}
