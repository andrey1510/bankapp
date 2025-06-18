package com.exchangeservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CurrencyRateDto(
    String title,
    String currency,
    BigDecimal value,
    LocalDateTime timestamp
) {}

