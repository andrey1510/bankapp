package com.accountservice.dto;

import java.util.List;

public record UserAccountsDto (
    String login,
    List<AccountInfoDto> accounts
) {
}
