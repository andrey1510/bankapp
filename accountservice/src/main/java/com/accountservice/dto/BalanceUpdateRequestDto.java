package com.accountservice.dto;

import java.math.BigDecimal;

public record BalanceUpdateRequestDto(
    Long senderAccountId,
    BigDecimal senderAccountBalanceChange,
    Long recipientAccountId,
    BigDecimal recipientAccountBalanceChange
) {}
