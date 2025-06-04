package com.frontservice.dto;

public record AccountInfoDto(
    Long accountId,
    String title,
    String currency,
    Double amount,
    boolean isEnabled
) {}
