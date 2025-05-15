package com.cashservice.dto;

import java.util.UUID;

public record AccountBalanceChange(
    UUID accountId,
    Double amount
) {}
