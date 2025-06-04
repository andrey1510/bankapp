package com.blockerservice.dto;

public record CashRequestDto(
    String email,
    Long accountId,
    String currency,
    Double amount
) {}
