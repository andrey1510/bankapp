package com.accountservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UserDto(
    @NotBlank String login,
    @NotBlank String password,
    @NotBlank String name,
    @Past LocalDate birthdate,
    @Email String email
) {}
