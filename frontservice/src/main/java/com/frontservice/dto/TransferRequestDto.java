package com.frontservice.dto;

public record TransferRequestDto(
    String email,
    Long senderAccountId,
    String senderAccountCurrency,
    Double amount,
    Long recipientAccountId,
    String recipientAccountCurrency
) {}
