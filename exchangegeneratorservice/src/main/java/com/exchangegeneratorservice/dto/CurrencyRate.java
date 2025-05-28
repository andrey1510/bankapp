package com.exchangegeneratorservice.dto;

import java.time.LocalDateTime;

public record CurrencyRate(
    String title,
    String name,
    Double value,
    LocalDateTime timestamp
) {}
