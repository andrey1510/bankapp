package com.transferservice.clients;

import com.transferservice.dto.BalanceUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AccountClient {

    private final RestTemplate restTemplate;

    @Value("${accountservice.url}")
    private String accountServiceUrl;

    public void updateBalances(BalanceUpdateRequest updateRequest) {
        restTemplate.postForObject(
            accountServiceUrl + "/balances/update",
            updateRequest,
            Void.class
        );
    }
}
