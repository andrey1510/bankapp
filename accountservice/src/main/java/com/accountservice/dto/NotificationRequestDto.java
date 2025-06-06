package com.accountservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record NotificationRequestDto(
    @Email String email,
    @NotNull String message
) {}
