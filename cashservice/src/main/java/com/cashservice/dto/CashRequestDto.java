package com.cashservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CashRequestDto(
    @Email String email,
    @NotNull Long accountId,
    @NotBlank String currency,
    @Positive Double amount,
    @NotNull boolean isDeposit
) {}
