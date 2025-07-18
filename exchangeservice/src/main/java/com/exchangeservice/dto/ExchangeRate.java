package com.exchangeservice.dto;

import java.math.BigDecimal;

public record ExchangeRate(
    String title,
    String currency,
    BigDecimal value
) {}
