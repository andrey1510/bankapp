package com.frontservice.dto;

import java.math.BigDecimal;

public record AccountUserInfoDto(
    String name,
    Long accountId,
    String email,
    String title,
    String currency,
    BigDecimal amount
) {}
