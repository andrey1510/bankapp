package com.blockerservice.dto;

import java.util.UUID;

public record TransferRequest(
    UUID senderAccountId,
    String senderAccountCurrency,
    Double amount,
    UUID recipientAccountId,
    String recipientAccountCurrency
) {}
