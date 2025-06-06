package com.frontservice.dto;

public record AccountUserInfoDto(
    String name,
    Long accountId,
    String email,
    String title,
    String currency,
    Double amount
) {}
