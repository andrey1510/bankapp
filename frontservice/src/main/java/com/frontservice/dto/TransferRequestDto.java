package com.frontservice.dto;

import java.math.BigDecimal;

public record TransferRequestDto(
    String email,
    Long senderAccountId,
    String senderAccountCurrency,
    BigDecimal amount,
    Long recipientAccountId,
    String recipientAccountCurrency,
    String senderLogin,
    String recipientLogin
) {}
