package com.cashservice.clients;

import com.cashservice.dto.AccountBalanceChangeDto;
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

    public void sendAccountRequest(Long accountId, Double amount) {
        restTemplate.postForObject(
            accountServiceUrl + "/cash",
            new AccountBalanceChangeDto(accountId, amount),
            Void.class
        );
    }

}
