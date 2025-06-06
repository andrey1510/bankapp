package com.accountservice.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeDto(
    @NotBlank String login,
    @NotBlank String password
) {}
