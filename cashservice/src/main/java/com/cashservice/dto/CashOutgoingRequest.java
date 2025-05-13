package com.cashservice.dto;

import java.util.UUID;

public record CashOutgoingRequest(
    UUID accountId,
    Double amount
) {}
