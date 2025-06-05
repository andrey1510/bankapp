package com.transferservice.dto;

public record BalanceUpdateRequestDto(
    Long senderAccountId,
    Double senderAccountBalanceChange,
    Long recipientAccountId,
    Double recipientAccountBalanceChange
) {}
