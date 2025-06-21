package com.transferservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequestDto(
    @Email String email,
    @NotNull Long senderAccountId,
    @NotBlank String senderAccountCurrency,
    @Positive BigDecimal amount,
    @NotNull Long recipientAccountId,
    @NotBlank String recipientAccountCurrency,
    @NotNull String senderLogin,
    @NotNull String recipientLogin
) {}
