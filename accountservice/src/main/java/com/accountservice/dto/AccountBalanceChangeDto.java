package com.accountservice.dto;

import java.math.BigDecimal;

public record AccountBalanceChangeDto(
    Long accountId,
    BigDecimal amount,
    String login
) {}
