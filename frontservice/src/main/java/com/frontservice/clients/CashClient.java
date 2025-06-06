package com.frontservice.clients;

import com.frontservice.dto.CashRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CashClient {

    private final RestTemplate restTemplate;

    public void sendCashRequest(CashRequestDto request) {
        restTemplate.postForEntity(
            "http://localhost:8883/api/cash/operation",
            request,
            Void.class
        );
    }

}
