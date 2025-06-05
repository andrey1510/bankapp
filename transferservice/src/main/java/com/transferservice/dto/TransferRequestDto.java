package com.transferservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransferRequestDto(
    @Email String email,
    @NotNull Long senderAccountId,
    @NotBlank String senderAccountCurrency,
    @Positive Double amount,
    @NotNull Long recipientAccountId,
    @NotBlank String recipientAccountCurrency
) {}
