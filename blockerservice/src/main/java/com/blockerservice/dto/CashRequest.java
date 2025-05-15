package com.blockerservice.dto;

import java.util.UUID;

public record CashRequest(
    String email,
    UUID accountId,
    String currency,
    Double amount
) {}
