package com.frontservice.dto;


import java.time.LocalDate;

public record UserUpdateDto(
    String login,
    String name,
    LocalDate birthdate,
    String email
) {}
