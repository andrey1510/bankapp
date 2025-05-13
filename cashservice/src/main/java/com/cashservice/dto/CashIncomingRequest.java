package com.cashservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CashIncomingRequest(
    @NotNull UUID accountId,
    @NotBlank String currency,
    @Positive Double amount
) {}
