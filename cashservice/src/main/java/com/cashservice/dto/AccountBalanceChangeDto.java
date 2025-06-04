package com.cashservice.dto;

public record AccountBalanceChangeDto(
    Long accountId,
    Double amount
) {}
