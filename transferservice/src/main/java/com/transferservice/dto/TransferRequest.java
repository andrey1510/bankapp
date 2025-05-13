package com.transferservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record TransferRequest(
    @NotNull UUID senderAccountId,
    @NotBlank String senderAccountCurrency,
    @Positive Double amount,
    @NotNull UUID recipientAccountId,
    @NotBlank String recipientAccountCurrency
) {}
