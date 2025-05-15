package com.cashservice.clients;

import com.cashservice.dto.AccountBalanceChange;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountClient {
    private final RestTemplate restTemplate;

    @Value("${accountservice.url}")
    private String accountServiceUrl;

    public void deposit(UUID accountId, Double amount) {
        restTemplate.postForObject(
            accountServiceUrl + "/deposit",
            new AccountBalanceChange(accountId, amount),
            Void.class
        );
    }

    public void withdraw(UUID accountId, Double amount) {
        restTemplate.postForObject(
            accountServiceUrl + "/withdraw",
            new AccountBalanceChange(accountId, amount),
            Void.class
        );
    }
}
