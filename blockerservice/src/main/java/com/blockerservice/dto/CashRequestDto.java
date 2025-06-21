package com.blockerservice.dto;

import java.math.BigDecimal;

public record CashRequestDto(
    String email,
    Long accountId,
    String currency,
    BigDecimal amount,
    String login
) {}
