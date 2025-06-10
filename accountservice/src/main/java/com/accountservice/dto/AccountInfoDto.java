package com.accountservice.dto;


import java.math.BigDecimal;

public record AccountInfoDto(
    Long accountId,
    String title,
    String currency,
    BigDecimal amount,
    boolean isExisting
) {}
