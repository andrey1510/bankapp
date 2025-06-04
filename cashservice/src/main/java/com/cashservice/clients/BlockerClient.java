package com.cashservice.clients;

import com.cashservice.dto.CashRequestDto;
import com.cashservice.dto.SuspicionOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class BlockerClient {

    private final RestTemplate restTemplate;

    @Value("${blockerservice.url}")
    private String baseUrl;

    public SuspicionOperation checkCashOperation(CashRequestDto request) {
        return restTemplate.postForObject(
            baseUrl + "/cash",
            request,
            SuspicionOperation.class
        );
    }
}
