package com.accountservice.dto;

import java.time.LocalDate;

public record UserDTO(
    String username,
    String firstName,
    String lastName,
    String email,
    String password,
    LocalDate birthday
) {}
