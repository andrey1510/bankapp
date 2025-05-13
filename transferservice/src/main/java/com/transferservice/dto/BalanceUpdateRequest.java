package com.transferservice.dto;

import java.util.UUID;

public record BalanceUpdateRequest(
    UUID senderAccountId,
    Double senderAccountBalanceChange,
    UUID recipientAccountId,
    Double recipientAccountBalanceChange
) {}
