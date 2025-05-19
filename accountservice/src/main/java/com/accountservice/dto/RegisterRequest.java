package com.accountservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record RegisterRequest(
    @NotBlank String login,
    @NotBlank String password,
    @NotBlank String name,
    @NotBlank String surname,
    @Past LocalDate dateOfBirth,
    @Email String email
) {}
