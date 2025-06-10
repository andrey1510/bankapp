package com.blockerservice.dto;

import java.math.BigDecimal;

public record TransferRequestDto(
    String email,
    Long senderAccountId,
    String senderAccountCurrency,
    BigDecimal amount,
    Long recipientAccountId,
    String recipientAccountCurrency
) {}
