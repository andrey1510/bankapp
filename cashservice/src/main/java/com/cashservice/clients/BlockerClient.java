package com.cashservice.clients;

import com.cashservice.dto.CashRequest;
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

    public SuspicionOperation checkCashOperation(CashRequest request) {
        return restTemplate.postForObject(
            baseUrl + "/cash",
            request,
            SuspicionOperation.class
        );
    }
}
