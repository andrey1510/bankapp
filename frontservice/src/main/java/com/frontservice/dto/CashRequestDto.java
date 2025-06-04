package com.frontservice.dto;


public record CashRequestDto(
    String email,
    Long accountId,
    String currency,
    Double amount,
    boolean isDeposit
) {}
