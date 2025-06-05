package com.transferservice.clients;

import com.transferservice.dto.BalanceUpdateRequestDto;
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

    public void updateBalances(BalanceUpdateRequestDto updateRequest) {
        restTemplate.postForObject(
            accountServiceUrl + "/transfer-update",
            updateRequest,
            Void.class
        );
    }
}
