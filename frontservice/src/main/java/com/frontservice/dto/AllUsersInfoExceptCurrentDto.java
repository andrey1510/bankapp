package com.frontservice.dto;

import java.util.List;

public record AllUsersInfoExceptCurrentDto(
    List<UserAccountsDto> users
) {}
