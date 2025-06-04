package com.frontservice.dto;

import java.time.LocalDate;

public record UserInfoDto(
    String login,
    String name,
    LocalDate birthdate,
    String email
) {}
