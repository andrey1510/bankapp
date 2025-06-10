package com.exchangeservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CurrencyRate(
    String title,
    String currency,
    BigDecimal value,
    LocalDateTime timestamp
) {}

