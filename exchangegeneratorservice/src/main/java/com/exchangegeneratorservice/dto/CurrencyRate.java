package com.exchangegeneratorservice.dto;

import java.time.LocalDateTime;

public record CurrencyRate(
    String title,
    String currency,
    Double value,
    LocalDateTime timestamp
) {}
