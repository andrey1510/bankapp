package com.accountservice.dto;

import java.util.List;

public record AllUsersInfoExceptCurrentDto(
    List<UserAccountsDto> users
) {}
