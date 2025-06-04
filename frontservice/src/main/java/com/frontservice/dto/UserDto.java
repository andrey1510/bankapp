package com.frontservice.dto;

import java.time.LocalDate;

public record UserDto(
    String login,
    String password,
    String name,
    LocalDate birthdate,
    String email
) {}
