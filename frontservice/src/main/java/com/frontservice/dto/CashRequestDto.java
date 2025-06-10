package com.frontservice.dto;


import java.math.BigDecimal;

public record CashRequestDto(
    String email,
    Long accountId,
    String currency,
    BigDecimal amount,
    boolean isDeposit
) {}
