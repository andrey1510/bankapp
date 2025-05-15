package com.cashservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record NotificationRequest(
    @Email String email,
    @NotNull String message
) {}
