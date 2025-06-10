package com.cashservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CashRequestDto(
    @Email String email,
    @NotNull Long accountId,
    @NotBlank String currency,
    @Positive BigDecimal amount,
    @NotNull boolean isDeposit
) {}
