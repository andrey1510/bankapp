package com.cashservice.dto;

import java.math.BigDecimal;

public record AccountBalanceChangeDto(
    Long accountId,
    BigDecimal amount
) {}
