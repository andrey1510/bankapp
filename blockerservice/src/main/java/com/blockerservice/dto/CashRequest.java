package com.blockerservice.dto;

import java.util.UUID;

public record CashRequest(
    UUID accountId,
    String currency,
    Double amount
) {}
